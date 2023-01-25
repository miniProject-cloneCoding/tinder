package clonecoding.tinder.member.entity;

public enum MemberRoleEnum {

    //현재 논의된 기능에선 굳이  authority 나눌 필요 없습니다
    //일단 security 적용하면서 userdetails 구현하는데, authority부분이 있어서 일단 만들었습니다!
    //진행하면서 필요없다고 판단되면 이 내용 지우도록 하겠습니다.

    //user를 사용하면 "ROLE_USER"를 리턴
    USER(Authority.USER);  // 사용자 권한


    private final String authority;


    MemberRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";

    }
}
