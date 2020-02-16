package com.jseastar.book.springboot.web.dto;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;


public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트(){
        //given
        String name = "test";
        int amount = 1000;

        //when
        HelloResponseDto dto = new HelloResponseDto(name, amount);

        //then
        /**
         * Assertj
         *   : CoreMatchers와 달리 추가 라이브러리 필요하지않음
         *    - Junit assertThat은 is() 처럼 라이브러리가 필요
         *   자동완성 확실히 지원
         *
         * assertThat : assertj 테스트검증 라이브러리의 검증 메소드
         * 메소드 체이닝 지원으로 .isEqaulTo 처럼 메소드 이어서 사용가능
         *
         *  isEqaulTo : assertj의 동등비교 메소드
         *  assertThat 에 있는 값과 isE--- 의 값을 비교해 같을때만 성공
          */
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);



    }

}