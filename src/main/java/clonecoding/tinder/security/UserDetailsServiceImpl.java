//package clonecoding.tinder.security;
//
//import clonecoding.tinder.member.entity.Member;
//import clonecoding.tinder.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//    private final MemberRepository memberRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String phoneNum) throws UsernameNotFoundException {
//        Member member = memberRepository.findByPhoneNum(phoneNum).orElseThrow(
//                () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")
//        );
//        return new UserDetailsImpl(member, member.getPhoneNum());
//    }
//}
