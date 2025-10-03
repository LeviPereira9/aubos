package lp.boble.aubos.service.book.dependencies.status;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.dependencies.StatusMapper;
import lp.boble.aubos.model.book.dependencies.StatusModel;
import lp.boble.aubos.repository.book.depedencies.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;

    public List<StatusResponse> getAllStatus(){
        return statusRepository.findAll().stream()
                .map(statusMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StatusModel getBookStatus(Integer id){
        return statusRepository.findById(id)
                .orElseThrow(CustomNotFoundException::status);
    }

}
