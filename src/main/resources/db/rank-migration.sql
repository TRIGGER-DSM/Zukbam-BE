-- 시작할 때마다 실행되므로 여러 번 돌려도 같은 결과여야 한다 (구분자는 @@)

-- Hibernate가 xp 컬럼을 먼저 만들었을 수도 있어서 두 경우를 모두 처리한다
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = current_schema() AND table_name = 'tbl_users' AND column_name = 'score') THEN
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema = current_schema() AND table_name = 'tbl_users' AND column_name = 'xp') THEN
            UPDATE tbl_users SET xp = score;
            ALTER TABLE tbl_users DROP COLUMN score;
        ELSE
            ALTER TABLE tbl_users RENAME COLUMN score TO xp;
        END IF;
    END IF;
END $$@@

-- 기존 기본 랭크를 이름만 바꿔 유지해서 이미 가입한 유저의 FK가 끊기지 않게 한다
UPDATE tbl_user_ranks SET name = '죽순'
WHERE name = 'Unranked'
  AND NOT EXISTS (SELECT 1 FROM tbl_user_ranks WHERE name = '죽순')@@

-- 이미 있는 랭크는 DB에서 조정한 값을 존중해 덮어쓰지 않는다
INSERT INTO tbl_user_ranks (name, score) VALUES
    ('죽순', 0),
    ('대나무 1m', 100),
    ('대나무 3m', 300),
    ('대나무 5m', 600),
    ('대나무 10m', 1600),
    ('금죽', 3000),
    ('오죽', 5000)
ON CONFLICT (name) DO NOTHING@@


-- 난이도 순서는 XP로 정하기로 해서 예전에 만든 순서 컬럼을 지운다. 남아 있으면 NOT NULL 때문에 시드가 들어가지 않는다
ALTER TABLE tbl_problem_ranks DROP COLUMN IF EXISTS sort_order@@

-- 이미 있는 문제 랭크는 DB에서 조정한 값을 존중해 덮어쓰지 않는다
INSERT INTO tbl_problem_ranks (name, xp) VALUES
    ('이슬', 10),
    ('물 한 컵', 20),
    ('물 한 병', 30),
    ('물 한 통', 50),
    ('비', 80)
ON CONFLICT (name) DO NOTHING@@

-- 예전 LEVEL_n 문자열을 같은 단계의 문제 랭크로 이름으로 옮긴다.
-- Hibernate가 NOT NULL 컬럼 추가에 실패했을 수도, 이미 추가했을 수도 있어서 둘 다 처리한다
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = current_schema() AND table_name = 'tbl_problems' AND column_name = 'problem_rank') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_schema = current_schema() AND table_name = 'tbl_problems' AND column_name = 'problem_rank_id') THEN
            ALTER TABLE tbl_problems ADD COLUMN problem_rank_id bigint;
        END IF;
        UPDATE tbl_problems p SET problem_rank_id = r.problem_rank_id
        FROM tbl_problem_ranks r
        WHERE r.name = CASE p.problem_rank
            WHEN 'LEVEL_1' THEN '이슬'
            WHEN 'LEVEL_2' THEN '물 한 컵'
            WHEN 'LEVEL_3' THEN '물 한 병'
            WHEN 'LEVEL_4' THEN '물 한 통'
            WHEN 'LEVEL_5' THEN '비'
        END;
        ALTER TABLE tbl_problems ALTER COLUMN problem_rank_id SET NOT NULL;
        ALTER TABLE tbl_problems DROP COLUMN problem_rank;
    END IF;
END $$@@

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_problems_problem_rank') THEN
        ALTER TABLE tbl_problems ADD CONSTRAINT fk_problems_problem_rank
            FOREIGN KEY (problem_rank_id) REFERENCES tbl_problem_ranks (problem_rank_id);
    END IF;
END $$@@
