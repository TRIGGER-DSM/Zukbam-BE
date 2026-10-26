-- JPA @Index로는 부분 인덱스를 만들 수 없어서 시작 시 직접 생성한다

-- 두 유저 사이에는 방향과 상관없이 대기 중이거나 수락된 친구 관계가 하나만 존재한다
CREATE UNIQUE INDEX IF NOT EXISTS uk_friendships_active_pair
    ON tbl_friendships (LEAST(requester_id, receiver_id), GREATEST(requester_id, receiver_id))
    WHERE status IN ('PENDING', 'ACCEPTED');

-- 한 그룹에서 같은 유저에게 대기 중인 초대는 하나만 존재한다
CREATE UNIQUE INDEX IF NOT EXISTS uk_group_invitations_pending
    ON tbl_group_invitations (group_id, invitee_id)
    WHERE status = 'PENDING';
