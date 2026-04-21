package com.bear.asset.service;

import com.bear.asset.dto.*;

public interface UserService {

    LoginResponse login(LoginRequest request);

    UserInfoResponse createUser(CreateUserRequest request);

    UserInfoResponse getUserById(Long userId);

    UserInfoResponse getCurrentUser();
}
