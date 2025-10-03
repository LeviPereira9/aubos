package lp.boble.aubos.service.book.dependencies.type;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.type.TypeRequest;
import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.TypeMapper;
import lp.boble.aubos.model.book.dependencies.TypeModel;
import lp.boble.aubos.repository.book.depedencies.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeService {

    private final TypeMapper typeMapper;
    private final TypeRepository typeRepository;

    public TypeModel getBookType(Integer id){
        return typeRepository.findById(id)
                .orElseThrow(CustomNotFoundException::type);
    }

    public List<TypeResponse> getAllTypes(){

        return typeRepository.findAll().stream()
                .map(typeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TypeResponse createType(TypeRequest request){
        this.validateType(request);

        TypeModel type = typeMapper.toModel(request);

        return typeMapper.toResponse(typeRepository.save(type));
    }

    @Transactional
    public TypeResponse updateType(Integer typeId, TypeRequest request){
        this.validateType(request);

        TypeModel type = this.getBookType(typeId);

        typeMapper.update(type, request);

        return typeMapper.toResponse(typeRepository.save(type));
    }

    private void validateType(TypeRequest request) {
        boolean hasConflict = typeRepository.existsByName(request.name());

        if(hasConflict){
            throw CustomDuplicateFieldException.type();
        }
    }

    @Transactional
    public void deleteType(Integer typeId){
        boolean exists = typeRepository.existsById(typeId);

        if(!exists){
            throw CustomNotFoundException.type();
        }

        typeRepository.deleteById(typeId);
    }



}
