package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    /**
     * 최대 허용 포인트 금액
     */
    private static final long MAX_POINT = 10000;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    /**
     * 현재 보유 포인트에서 충전 포인트를 더한 객체 반환
     */
    public UserPoint chargePointWithMaxPoint(long chargePoint) {

        if (this.point + chargePoint > MAX_POINT) {
            throw new IllegalArgumentException("충전할 수 있는 최대 포인트를 초과 했습니다.");
        }

        if (chargePoint <= 0) {
            throw new IllegalArgumentException("마이너스 금액 또는 0원을 충전할 수 없습니다.");
        }

        return new UserPoint(this.id, this.point + chargePoint, System.currentTimeMillis());
    }
}
