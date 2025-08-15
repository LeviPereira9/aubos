package lp.boble.aubos.service.apikey;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.apikey.ApiKeyCreateResponse;
import lp.boble.aubos.dto.apikey.ApiKeyResponse;
import lp.boble.aubos.exception.custom.apikey.CustomApiKeyGenerationException;
import lp.boble.aubos.exception.custom.apikey.CustomApiKeyValidationException;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.auth.CustomHashGenerationException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.apikey.ApiKeyMapper;
import lp.boble.aubos.model.apikey.ApiKeyModel;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.apikey.ApiKeyRepository;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int HASH_ITERATIONS = 1024;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final int SECRET_LENGTH = 32;
    private static final String KEY_DELIMITER = "\\.";
    private static final String KEY_PREFIX = "client_";
    private static final int MAX_KEYS_PER_USER = 1;
    private static final Duration REVOCATION_GRACE_PERIOD = Duration.ofHours(6);

    private final SecureRandom secureRandom = new SecureRandom();
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final ApiKeyMapper apiKeyMapper;

    private final AuthUtil authUtil;


    /**
     * Gera e armazena uma nova API Key com todos os requisitos de segurança.
     *
     * @param username Identificação do usuário que receberá a chave
     * @return {@link ApiKeyCreateResponse} contendo: <br>
     *         * ID interno da chave <br>
     *         * Chave completa no formato "publicId:rawSecret" (exibida apenas uma vez) <br>
     * @throws CustomNotFoundException em casos de:
     * * Usuário não encontrado.
    */
    public ApiKeyCreateResponse generateApiKey(String username) {

        authUtil.isNotSelfOrAdmin(username);

        // 1 - Buscar o usuário que quer criar a chave.
        UserModel owner = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        // Verificar o limite de chaves do usuário
        validateKeyLimit(owner.getUsername());

        // 2 - Cria o rawSecret em 32 Bytes
        byte[] randomBytes = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(randomBytes);

        // 3 - Codifica para String.
        String rawSecret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // 4 - Cria o secret adicionando o salt
        String hashedSecret = generateHashSecret(rawSecret);

        // 5 - Gera o publicId, que será o único identificador daqui pra frente.
        String publicId = KEY_PREFIX + UUID.randomUUID().toString().replaceAll("-", "");

        // 6 - Cria a entidade para armazenamento
        ApiKeyModel apiKeyToSave = new ApiKeyModel();
        apiKeyToSave.setPublicId(publicId);
        apiKeyToSave.setHashedSecret(hashedSecret);
        apiKeyToSave.setOwner(owner);
        apiKeyToSave.setCreatedAt(Instant.now());
        apiKeyToSave.setResetAt(generateResetTime());
        apiKeyToSave.setExpiresAt(generateApiKeyExpiryDate());

        ApiKeyModel apiKeyCreated = apiKeyRepository.save(apiKeyToSave);

        // 7 - Retorna a resposta com a chave para o usuário.
        return new ApiKeyCreateResponse(
            apiKeyCreated.getId(),
            apiKeyCreated.getPublicId() + ":" + rawSecret
        );
    }

    /**
     * Gera um hash seguro a partir de um segredo, incluindo salt aleatório.
     *
     * @param secret O segredo a ser hasheado (senha/chave em formato String)
     * @return String no formato "saltBase64.hashBase64" pronto para armazenamento seguro

    */
    private String generateHashSecret(String secret){
        try {
            // 1 - Gerar salt
            byte[] salt = generateSalt();

            // 2- Criar o hash com secret e o salt gerados.
            byte[] hash = generateHash(secret.toCharArray(), salt);

            // 3 - Preparar para armazenar
            return encodeForStorage(salt, hash);
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            throw CustomHashGenerationException.failedToGenerateHash();
        }
    }

    /**
     * Gera um valor de salt criptograficamente seguro para uso em derivação de chaves.
     *
     * @return Byte array contendo os bytes aleatórios do salt
     * @throws IllegalStateException Se o SALT_LENGTH for menor que 16 bytes

    */
    private byte[] generateSalt() throws NoSuchAlgorithmException {
        // Validação de pré-condições
        if (SALT_LENGTH < 16) {
            throw new IllegalStateException("Tamanho do salt deve ser ≥16 bytes para segurança");
        }

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Gera um hash seguro usando PBKDF2 com HMAC-SHA256.
     *
     * @param secret O segredo a ser hasheado (em char[] para permitir limpeza segura)
     * @param salt O valor de salt aleatório (em byte[] deve ser único por hash)
     * @return Hash derivado como byte[] no tamanho especificado por KEY_LENGTH
     * @throws NoSuchAlgorithmException em caso de: <br>
     * - Se o algoritmo não estiver disponível
     * @throws InvalidKeySpecException em caso de: <br>
     * - Se a especificação da chave for inválida
     * @throws CustomFieldNotProvided em caso de: <br>
     * - Se a chave não for passada corretamente
     * @throws CustomApiKeyGenerationException em case de: <br>
     * - Falha na geração de Hash.
    */
    private byte[] generateHash(char[] secret, byte[] salt)
    throws NoSuchAlgorithmException, InvalidKeySpecException {

        if (secret == null || secret.length == 0) {
            throw CustomFieldNotProvided.key();
        }

        if (salt == null || salt.length < 8) { // Mínimo 8 bytes
            throw CustomFieldNotProvided.key();
        }

        PBEKeySpec spec = new PBEKeySpec(
            secret,
            salt,
            HASH_ITERATIONS,
            KEY_LENGTH
        );

        try {

            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        }catch(Exception e){
            throw CustomApiKeyGenerationException.generateHash();
        } finally {
            spec.clearPassword();
        }
    }

    /**
    * Codifica o salt e hash para armazenamento seguro em formato String.
    *
    * @param salt O valor de salt aleatório em bytes (em byte[], geralmente 16+ bytes)
    * @param hash O hash resultante em bytes (em byte[], gerado pelo algoritmo PBKDF2/bcrypt)
    * @return String no formato "saltBase64.hashBase64" onde: <br>
    *         - saltBase64: Representação Base64 URL-safe do salt <br>
    *         - hashBase64: Representação Base64 URL-safe do hash <br>
    *         - O delimitador "." é usado para separar os componentes
    */
    private String encodeForStorage(byte[] salt, byte[] hash) {
        // Salt e Hash em base64
        return Base64.getUrlEncoder().encodeToString(salt)
               + "." +
               Base64.getUrlEncoder().encodeToString(hash);
    }

    /**
    * Verifica se um segredo fornecido corresponde ao hash armazenado.
    *
    * @param inputSecret O segredo fornecido para verificação (em formato String)
    * @param storedHash O hash armazenado no formato "salt:hash" (em formato String, codificado em Base64 URL-safe)
    * @return true se o segredo for válido, false caso contrário ou em caso de erro
    */
    public boolean verifySecret(String inputSecret, String storedHash) {
        try{
            String[] parts = storedHash.split(KEY_DELIMITER);
            if(parts.length != 2) {
                return false;
            }

            byte[] originalSalt = Base64.getUrlDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getUrlDecoder().decode(parts[1]);

            byte[] testHash = generateHash(inputSecret.toCharArray(), originalSalt);

            return MessageDigest.isEqual(originalHash, testHash);
        } catch (Exception e){
            return false;
        }

    }

    /**
    * Valida uma API Key conforme os critérios de segurança e limites de uso.
    *
    * @param apiKey A chave de API no formato "publicId.secret" recebida na requisição (em formato String)
    * @return  {@link ApiKeyModel}
    * @throws CustomFieldNotProvided em caso de: <br>
     * - Api Key fornecido não ter public id e secret.
     * @throws CustomNotFoundException em caso de: <br>
     * - ApiKey não ser achada <br>
     * - O hash gerado com a apiKey fornecida não bater com a hash armazenada.
     * @throws CustomApiKeyValidationException em caso de: <br>
     * - Chave ter atingido o limite de chamadas do dia. <br>
     * - Chave ter expirado.
    */
    public ApiKeyModel validateApiKey(String apiKey) {
        String[] parts = apiKey.split(":");
        if(parts.length != 2) {
            throw CustomFieldNotProvided.key();
        }

        String publicId = parts[0];
        String providedSecret = parts[1];
        boolean isFine;

        ApiKeyModel key = apiKeyRepository.findByPublicId(publicId)
                .orElseThrow(CustomNotFoundException::key);

        if(key.getPreviousHashedSecret() != null){
            isFine =
                    verifySecret(providedSecret, key.getPreviousHashedSecret())
                    | // OU
                    verifySecret(providedSecret, key.getHashedSecret());
        } else {
            isFine = verifySecret(providedSecret, key.getHashedSecret());
        }

        if(!isFine) {
            throw CustomNotFoundException.key();
        }

        if(key.getRequestCount() >= key.getRateLimit()){
            throw CustomApiKeyValidationException.rateLimit();
        }

        if(key.isExpired()){
            throw CustomApiKeyValidationException.expired();
        }

        //Atualiza os campos
        key.incrementRequestCount();
        key.setLastUsedAt(Instant.now());

        apiKeyRepository.save(key);

        return key;

    }

    /**
    * Gera o instant de reset para contagem de requisições, configurado para 1 mês no futuro
    * no fuso horário de Brasília (UTC-3).
    *
    * @return {@link Instant} representando: <br>
    *         - Data/hora atual + 31 dias (para garantir cobertura mensal) <br>
    *         - Convertido para UTC-3 (Horário de Brasília padrão) <br>
    *         - Ajustado para o início do próximo ciclo (meia-noite) <br>
    */
    private Instant generateResetTime() {
        ZoneId brasiliaZone = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime now = ZonedDateTime.now(brasiliaZone);

        // Ajusta para o início do dia + 1 mês
        ZonedDateTime resetDateTime = now.plusMonths(1)
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0);

        return resetDateTime.toInstant();
    }


    /**
     * Valida se o usuário pode criar novas chaves conforme políticas.
     * @param username passando o {@link UserModel}
     * @throws CustomApiKeyValidationException em caso de: <br>
     * - O número de chaves que o usuário possui for maior que {@value MAX_KEYS_PER_USER}
     */
    private void validateKeyLimit(String username){
        long activeKeys = apiKeyRepository.countByOwnerAndStatus(username);

        if(activeKeys >= MAX_KEYS_PER_USER){
            throw CustomApiKeyValidationException.keyLimitExceeded();
        }
    }

    /**
     * Calcula data de expiração padrão (1 ano).
     */
    private Instant generateApiKeyExpiryDate() {
        return Instant.now().plus(365, ChronoUnit.DAYS);
    }

    /**
     * Buscar todas as chaves de API de um usuário.
     *
     * @param username em formato String
     * @return {@link List<ApiKeyResponse>} uma lista com todas as keys.
     * @throws CustomFieldNotProvided em caso de: <br>
     * - Username não fornecido.
     * @throws CustomNotFoundException em caso de: <br>
     * - Usuário não encontrado
     * */
    public List<ApiKeyResponse> findAllUserKeys(String username){

        authUtil.isNotSelfOrAdmin(username);

        List<ApiKeyModel> keys = apiKeyRepository.findAllByOwner(username);

        return keys
                .stream()
                .map(apiKeyMapper::fromModelToResponse)
                .toList();

    }

    /**
     * Soft delete em uma Chave Api
     * @param username username de dono da chave (em formato String)
     * @param publicId public id da chave api (em formato String)
     * @throws CustomFieldNotProvided em caso de: <br>
     * - Username não fornecido. <br>
     * - public id não fornecido.
     * @throws CustomForbiddenActionException em caso de: <br>
     * - Requester não ser self ou admin
     * @throws CustomNotFoundException em caso de: <br>
     * - Não achar a Chave pelo public id
     * */
    public void disableApiKey(String username, String publicId) {

        authUtil.isNotSelfOrAdmin(username);

        if(publicId.isBlank()){
            throw CustomFieldNotProvided.key();
        }

        ApiKeyModel toDelete = apiKeyRepository.findByPublicIdAndOwnerUsername(publicId, username)
                .orElseThrow(CustomNotFoundException::key);

        toDelete.setSoftDelete(true);

        apiKeyRepository.save(toDelete);
    }

    /**
     * Rotaciona a chave
     * @param username (string)
     * @param publicId (string)
     * @return {@link ApiKeyCreateResponse}
     * <hr>
     * @throws CustomFieldNotProvided Em caso de: <br>
     * - Username vazio
     * @throws CustomNotFoundException Em caso de: <br>
     * - Chave não encontrada <br>
     * @throws CustomForbiddenActionException Em caso de: <br>
     * - Não ser ADM ou Requester
     * */
    public ApiKeyCreateResponse rotateKey(String username, String publicId){
        authUtil.isNotSelfOrAdmin(username);

        ApiKeyModel key = apiKeyRepository.findByPublicIdAndOwnerUsername(publicId, username)
                .orElseThrow(CustomNotFoundException::key);

        byte[] randomBytes = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(randomBytes);

        String rawSecret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String hashedSecret = generateHashSecret(rawSecret);

        key.setPreviousHashedSecret(key.getHashedSecret());
        key.setHashedSecret(hashedSecret);
        key.setRotatedAt(Instant.now());

        apiKeyRepository.save(key);

        return new ApiKeyCreateResponse(
                key.getId(),
                key.getPublicId() + ":" + rawSecret
        );
    }

    @Transactional
    @Scheduled(cron = "0 0 */1 * * *")
    public void scheduleToRevokePreviousHash(){
        Instant sixHoursAgo = Instant.now().minus(REVOCATION_GRACE_PERIOD);
        apiKeyRepository.revokePreviousHash(sixHoursAgo);
    }


    /**
     * Revoga a chave prévia a rotação
     * @param username (string)
     * @param publicId (string)
     * <hr>
     * @throws CustomFieldNotProvided Em caso de: <br>
     * - Username vazio
     * @throws CustomNotFoundException Em caso de: <br>
     * - Chave não encontrada <br>
     * @throws CustomForbiddenActionException Em caso de: <br>
     * - Não ser ADM ou Requester
     * */
    public void revokePreviousHashSecret(String username, String publicId){
        authUtil.isNotSelfOrAdmin(username);

        ApiKeyModel key = apiKeyRepository.findByPublicIdAndOwnerUsername(publicId, username)
                .orElseThrow(CustomNotFoundException::key);

        key.setPreviousHashedSecret(null);
        apiKeyRepository.save(key);
    }

}
