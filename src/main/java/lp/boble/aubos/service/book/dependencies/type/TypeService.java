package lp.boble.aubos.service.book.dependencies.type;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.type.TypeResponse;
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

}
