package lp.boble.aubos.service.book.dependencies.status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.status.StatusRequest;
import lp.boble.aubos.dto.book.dependencies.status.StatusResponse;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
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

    @Transactional
    public StatusResponse createStatus(StatusRequest request){
        this.validateStatusRequest(request);

        StatusModel status = statusMapper.toModel(request);

        return statusMapper.toResponse(statusRepository.save(status));
    }

    @Transactional
    public StatusResponse updateStatus(Integer statusId, StatusRequest request){
        this.validateStatusRequest(request);

        StatusModel status = this.getBookStatus(statusId);

        statusMapper.update(status, request);

        return statusMapper.toResponse(statusRepository.save(status));
    }

    private void validateStatusRequest(StatusRequest request) {
        String requestedLabel = request.label();
        boolean hasConflict = statusRepository.existsByLabel(requestedLabel);

        if(hasConflict){
            throw CustomDuplicateFieldException.status();
        }

    }

    @Transactional
    public void deleteStatus(Integer id){
        boolean exists = statusRepository.existsById(id);

        if(!exists){
            throw CustomNotFoundException.status();
        }

        statusRepository.deleteById(id);
    }

}
