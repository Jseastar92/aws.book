package com.jseastar.book.springboot.config.auth;

import com.jseastar.book.springboot.config.auth.dto.OAuthAttributes;
import com.jseastar.book.springboot.domain.user.User;
import com.jseastar.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * 1 : 현재 진행중인 서비스를구분하는 코드 지금은 구글만 사용하는 불필요한 값,
         * 이후 네이버 연동시 네이버인지 구글인지 구분하기 위해 사용
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        /**
         * Oauth2 로그인 진행시 키가 되는 필드값(PK같은 의미)
         * 구글은 기본적으로 코드 지원 // 네이버 카카오는 지원하지않는다. ( 구글 기본코드는 sub)
         * 이후 네이버/구글 로그인 동시 지원할 때 사용
         */
        String userNameAttributeName =
                userRequest
                        .getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName();


        /**
         * OAuthUserService를 통해 가져온 OAuthUser의 attribute를 담을 클래스
         * 이후 네이버등 다른 소셜로그인도 이 클래스를 사용.
         */
        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        /**
         * 세션에 사용자 정보를 저장하기위한 DTO
         * User 클래스를 쓰지 않고 새로 만들어서 쓴다! (추후설명!)
         */
        httpSession.setAttribute("user", new SessionUser(user)); // 4

        return new DefaultOAuth2User(
                Collections
                        .singleton(new SimpleGrantedAuthority(user.getRoleKey()))
                , attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
