package com.forcard.core.usermanagement.service;

import com.forcard.api.rest.request.UserRequest;
import com.forcard.api.rest.response.UserResponse;
import com.forcard.core.shared.exception.EntityNotFoundException;
import com.forcard.core.shared.utils.time.TimeService;
import com.forcard.core.usermanagement.mapper.UserRequestMapper;
import com.forcard.core.usermanagement.mapper.UserResponseMapper;
import com.forcard.core.usermanagement.model.User;
import com.forcard.core.usermanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.forcard.core.shared.utils.InfoUtils.NOT_FOUND;

@Slf4j
@Service
public class UserSaveService {

    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final UserRepository userRepository;
    private final TimeService timeService;


    public UserSaveService(UserRequestMapper userRequestMapper,
                           UserResponseMapper userResponseMapper,
                           UserRepository userRepository,
                           TimeService timeService) {
        this.userRequestMapper = userRequestMapper;
        this.userResponseMapper = userResponseMapper;
        this.userRepository = userRepository;
        this.timeService = timeService;
    }

    public ResponseEntity<?> updateUser(UserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
        user.setModifiedAt(timeService.getLocalDateTime());
        User updatedUser = userRequestMapper.mapToDomainObject(user, request);
        userRepository.save(updatedUser);
        log.info("User with id: {} successfully updated!", request.getId().toHexString());
        UserResponse userResponse = userResponseMapper.simplifyToRestObject(updatedUser);
        return ResponseEntity.ok().body(userResponse);
    }

}
