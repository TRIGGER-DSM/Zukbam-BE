-- JPA Auditing(@CreatedDate, @LastModifiedDate)은 지금까지 JVM 시간대로 시각을 채웠고, 서버 컨테이너의 JVM 시간대는 UTC다.
-- 이번 버전부터 Auditing도 ClockConfig의 한국 시간 시계를 쓰므로, 이미 UTC로 저장된 값을 한국 시간으로 옮긴다.
--
-- 처음부터 Clock(한국 시간)으로 넣던 컬럼은 건드리지 않는다:
--   tbl_solved_histories.solved_time, tbl_friendships.accepted_at, tbl_group_invitations.expires_at,
--   tbl_quest_rewards.rewarded_at, tbl_xp_histories.created_at
--
-- 주의: JVM 시간대가 이미 한국 시간인 환경(맥에서 bootRun한 로컬 DB)에서는 값이 9시간 밀린다.
-- 서버(stag, prod)와 새로 만든 DB(CI)에서는 맞다.

UPDATE tbl_users
SET created_at = created_at + INTERVAL '9 hours',
    updated_at = updated_at + INTERVAL '9 hours';

UPDATE tbl_solutions SET submitted_at = submitted_at + INTERVAL '9 hours';

UPDATE tbl_friendships SET created_at = created_at + INTERVAL '9 hours';

UPDATE tbl_groups SET created_at = created_at + INTERVAL '9 hours';

UPDATE tbl_group_members SET joined_at = joined_at + INTERVAL '9 hours';

UPDATE tbl_group_invitations SET created_at = created_at + INTERVAL '9 hours';

UPDATE tbl_notices SET created_at = created_at + INTERVAL '9 hours';
