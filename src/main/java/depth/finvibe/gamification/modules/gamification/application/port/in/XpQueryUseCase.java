package depth.finvibe.gamification.modules.gamification.application.port.in;

public interface XpQueryUseCase {
    //1. 내 현재 XP 조회
    // - 주간 XP 변동률 (%단위)

    //3. 스쿼드 XP 랭킹 조회 
    // - 대학이 가지고있는 총 XP의 합을 기준으로 랭킹 산정.
    // - (현재랭킹, 총 XP, 이번주 얻은 XP, 이번주 XP 변동률 (%단위), 랭킹 등락폭[+2, -2, ... 등]) 제공

    //4. 우리 학교 기여도 랭킹 조회 (학교 구성원중중)
    // - 학교 구성원중 가장 많은 XP를 얻은 사람을 찾아서 랭킹 산정.
    // - (구성원 닉네임,현재랭킹, 이번주 기여한 XP) 제공

    //TODO: 스쿼드 XP 랭킹 관련 도메인 모델 추가 필요
}
