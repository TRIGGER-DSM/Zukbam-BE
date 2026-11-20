-- 어셈블리(NASM x64) 제출을 허용한다
ALTER TABLE tbl_solutions DROP CONSTRAINT tbl_solutions_language_check;
ALTER TABLE tbl_solutions ADD CONSTRAINT tbl_solutions_language_check
    CHECK (language IN ('C99', 'CPP17', 'JAVA', 'PYTHON', 'JAVASCRIPT', 'ASSEMBLY', 'TEXT'));
