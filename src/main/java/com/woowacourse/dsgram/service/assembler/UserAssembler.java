package com.woowacourse.dsgram.service.assembler;

import com.woowacourse.dsgram.domain.User;
import com.woowacourse.dsgram.service.dto.user.LoginUserDto;
import com.woowacourse.dsgram.service.dto.user.SignUpUserDto;
import com.woowacourse.dsgram.service.dto.user.UserDto;

public class UserAssembler {
    public static User toEntity(SignUpUserDto signUpUserDto) {
        return User.builder()
                .email(signUpUserDto.getEmail())
                .nickName(signUpUserDto.getNickName())
                .password(signUpUserDto.getPassword())
                .userName(signUpUserDto.getUserName())
                .intro("")
                .webSite("")
                .build();
    }

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .userName(userDto.getUserName())
                .password(userDto.getPassword())
                .nickName(userDto.getNickName())
                .intro(userDto.getIntro())
                .webSite(userDto.getWebSite())
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .intro(user.getIntro())
                .nickName(user.getNickName())
                .userName(user.getUserName())
                .password(user.getPassword())
                .webSite(user.getWebSite())
                .build();
    }

    public static LoginUserDto toAuthUserDto(User user) {
        return LoginUserDto.builder()
                .nickName(user.getNickName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
}