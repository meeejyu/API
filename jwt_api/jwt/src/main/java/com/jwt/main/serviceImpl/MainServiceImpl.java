package com.jwt.main.serviceImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwt.comm.util.JwtTokenUtil;
import com.jwt.config.cache.CacheKey;
import com.jwt.config.jwt.JwtExpirationEnums;
import com.jwt.domain.LoginDto;
import com.jwt.domain.TokenDto;
import com.jwt.domain.jwt.LogoutAccessToken;
import com.jwt.domain.jwt.LogoutAccessTokenRedisRepository;
import com.jwt.domain.jwt.RefreshToken;
import com.jwt.domain.jwt.RefreshTokenRedisRepository;
import com.jwt.main.mapper.MainMapper;
import com.jwt.main.service.MainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MainServiceImpl implements MainService{
    
    private final MainMapper mainMapper;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> memberLoginId(String loginId) {
        Map<String, Object> map = mainMapper.memberLoginId(loginId);        
        return map;
    }

    @Override
    public Map<String, Object> memberUsername(String username) {
        Map<String, Object> memberMap = mainMapper.memberUsername(username);        
        return memberMap;
    }

    @Override
    public Map<String, Object> getAuthority(String id) {
        Map<String, Object> memberMap = mainMapper.getAuthority(id);        
        return memberMap;
    }

    @Override
    public Map<String, Object> getLoginMember(LoginDto loginDto) {

        Map<String, Object> memberMap = mainMapper.getLoginMember(loginDto);
        
        return memberMap;
    }

    private String resolveToken(String token) {
        return token.substring(7);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }

    private TokenDto reissueRefreshToken(String refreshToken, String username) {
        if(lessThanReissueExpirationTimesLeft(refreshToken)) {
            String accessToken = jwtTokenUtil.generateAccessToken(username);
            return TokenDto.of(accessToken, saveRefreshToken(username).getRefreshToken());
        }
        return TokenDto.of(jwtTokenUtil.generateAccessToken(username), refreshToken);
    }

    private boolean lessThanReissueExpirationTimesLeft(String refreshToken) {
        return jwtTokenUtil.getRemainMilliSeconds(refreshToken) < JwtExpirationEnums.REISSUE_EXPIRATION_TIME.getValue();
    }

    public TokenDto reissue(String refreshToken) {
        refreshToken = resolveToken(refreshToken);
        String username = getCurrentUsername();
        // 리프레시 토큰 hash에서 값을 가져옴
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(username).get();
            if(refreshToken.equals(redisRefreshToken.getRefreshToken())) {
                return reissueRefreshToken(refreshToken, username);
            }   
        throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    }

    public TokenDto getLoginMemberToken(LoginDto loginDto) {

        Map<String, Object> memberMap = mainMapper.getLoginMember(loginDto);
        
        String username = (String) memberMap.get("MEMBER_USERNAME");
        String accessToken = jwtTokenUtil.generateAccessToken(username);
        RefreshToken refreshToken = saveRefreshToken(username);

        return TokenDto.of(accessToken, refreshToken.getRefreshToken());
    }

    // 토큰 생성완료 저장됨
    private RefreshToken saveRefreshToken(String username) {
        return refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(
            username, jwtTokenUtil.generateRefreshToken(username), JwtExpirationEnums.REFRESH_TOKEN_EXPIRATION_TIME.getValue()));
    }

    @CacheEvict(value = CacheKey.USER, key= "#username")
    public void logout(TokenDto tokenDto, String username) {
        String accessToken = resolveToken(tokenDto.getAccessToken());
        long remainMilliSeconds = jwtTokenUtil.getRemainMilliSeconds(accessToken);
        refreshTokenRedisRepository.deleteById(username);
        logoutAccessTokenRedisRepository.save(LogoutAccessToken.of(accessToken, username, remainMilliSeconds));
    }

    @CacheEvict(value = CacheKey.USER, key= "#username")
    public void refreshTokenDelete(TokenDto tokenDto, String username) {
        refreshTokenRedisRepository.deleteById(username);
    }




// 2번쨰 방법 : redisRepositoryConfig를 통해 키 저장

    // public TokenDto reissue(String refreshToken) {
    //     refreshToken = resolveToken(refreshToken);
    //     String username = getCurrentUsername();
    //     // 리프레시 토큰 hash에서 값을 가져옴

    //     // ValueOperations<String, String> valueOperations = redisTemplate.opsForHash();
    //     RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(username).get();

    //         if(refreshToken.equals(redisRefreshToken.getRefreshToken())) {
    //             return reissueRefreshToken(refreshToken, username);
    //         }   
    //     throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    // }

    // // 토큰 생성완료 저장됨
    // private RefreshToken saveRefreshToken(String username) {
    //     return refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(
    //         username, jwtTokenUtil.generateRefreshToken(username), JwtExpirationEnums.REFRESH_TOKEN_EXPIRATION_TIME.getValue()));
    // }

    // @CacheEvict(value = CacheKey.USER, key= "#username")
    // public void logout(TokenDto tokenDto, String username) {
    //     String accessToken = resolveToken(tokenDto.getAccessToken());
    //     long remainMilliSeconds = jwtTokenUtil.getRemainMilliSeconds(accessToken);
    //     refreshTokenRedisRepository.deleteById(username);
    //     logoutAccessTokenRedisRepository.save(LogoutAccessToken.of(accessToken, username, remainMilliSeconds));
    // }

    // @CacheEvict(value = CacheKey.USER, key= "#username")
    // public void refreshTokenDelete(TokenDto tokenDto, String username) {
    //     refreshTokenRedisRepository.deleteById(username);
    // }


}
