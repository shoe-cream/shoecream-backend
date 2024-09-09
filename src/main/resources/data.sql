
-- 팀장 데이터 추가
INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('TL001', 'admin@gmail.com', '팀장님', '{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-1234-5678', '123 Leader Street');

-- 나머지 9명의 멤버 추가
INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('EMP002', 'hyeji@company.com', '박혜지','{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-5678-0002', 'Address 2');

INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('EMP003', 'jhj@company.com', '조한재', '{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-5678-0003', 'Address 3');

INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('EMP004', 'pyy@company.com', '박이연', '{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-5678-0004', 'Address 4');

INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('EMP005', 'nyj@company.com', '노영준', '{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-5678-0005', 'Address 5');

INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('EMP006', 'hjh@company.com', '황진혁', '{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-5678-0006', 'Address 6');
