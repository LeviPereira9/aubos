package lp.boble.aubos.service.book.family;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.family.FamilyRequest;
import lp.boble.aubos.dto.book.family.FamilyResponse;
import lp.boble.aubos.dto.book.family.FamilyTypeResponse;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.family.FamilyMapper;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.family.FamilyType;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.book.family.FamilyRepository;
import lp.boble.aubos.repository.book.family.FamilyTypeRepository;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final FamilyTypeRepository familyTypeRepository;
    private final AuthUtil authUtil;
    private final FamilyMapper familyMapper;

    @Transactional
    public FamilyResponse createFamily(FamilyRequest familyRequest) {
        FamilyModel family = new FamilyModel();
        family.setName(familyRequest.name());
        family.setCreatedBy(authUtil.getRequester());

        FamilyType familyType = this.findFamilyTypeOrThrow(familyRequest.type());
        family.setType(familyType);

        return familyMapper.toResponse(familyRepository.save(family));
    }

    @Transactional
    public FamilyResponse updateFamily(UUID id, FamilyRequest familyRequest) {
        FamilyModel family = this.findFamilyOrThrow(id);
        family.setUpdatedBy(authUtil.getRequester());

        familyMapper.updateFamily(family, familyRequest);

        if(!Objects.equals(family.getType().getId(), familyRequest.type())){
            FamilyType type = this.findFamilyTypeOrThrow(familyRequest.type());
            family.setType(type);
        }

        return familyMapper.toResponse(familyRepository.save(family));
    }

    @Transactional
    public void deleteFamily(UUID id) {
        this.validateOwnershipOrThrow(id);

        familyRepository.deleteById(id);
    }

    public FamilyResponse getFamily(UUID id) {
        return familyMapper.toResponse(this.findFamilyOrThrow(id));
    }

    public List<FamilyTypeResponse> getAllTypes() {
        return familyTypeRepository.findAll().stream()
                .map(familyMapper::toTypeResponse).collect(Collectors.toList());
    }

    private FamilyModel findFamilyOrThrow(UUID id){
        return familyRepository.findById(id).orElseThrow(CustomNotFoundException::family
        );
    };

    private FamilyType findFamilyTypeOrThrow(Long id){
        return familyTypeRepository.findById(id).orElseThrow(CustomNotFoundException::familyType);
    }

    private void validateOwnershipOrThrow(UUID id){
        if(!familyTypeRepository.isOwner(id, authUtil.getRequester()) && !authUtil.isAdmin()){
            throw CustomForbiddenActionException.notSelfOrAdmin();
        }
    }
}
