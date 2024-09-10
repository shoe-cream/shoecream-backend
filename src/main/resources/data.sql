
-- 팀장 데이터 추가
INSERT INTO member (employee_id, email, name, password, tel, address)
VALUES ('TL001', 'admin@gmail.com', '팀장님', '{bcrypt}$2a$10$ZuxK7vHU5jpTbednD.zrYOsJgm48HiBTMPBYfJTGIfzrh.nqxxgXq', '010-1234-5678', '123 Leader Street');

-- 멤버 추가
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

--권한 추가
INSERT INTO MEMBER_ROLES(MEMBER_MEMBER_ID, ROLES) VALUES
('1', 'ADMIN'),
('1', 'USER'),
('2', 'USER'),
('3', 'USER'),
('4', 'USER'),
('5', 'USER'),
('6', 'USER');

-- Dummy data for Manufacture
INSERT INTO manufacture (region, email, mf_cd, mf_nm, created_at, manufacture_status) VALUES
('Germany', 'mainplant@adidas.com', 'MF001', 'Adidas Main Plant Germany', NOW(), 'ACTIVE'),
('Vietnam', 'vnfactory@adidas.com', 'MF002', 'Adidas Vietnam Factory', NOW(), 'ACTIVE'),
('China', 'cnplant@adidas.com', 'MF003', 'Adidas China Factory', NOW(), 'ACTIVE'),
('USA', 'usfactory@adidas.com', 'MF004', 'Adidas USA Factory', NOW(), 'ACTIVE'),
('India', 'indfactory@adidas.com', 'MF005', 'Adidas India Factory', NOW(), 'ACTIVE'),
('Bangladesh', 'bdplant@adidas.com', 'MF006', 'Adidas Bangladesh Plant', NOW(), 'ACTIVE'),
('Italy', 'itplant@adidas.com', 'MF007', 'Adidas Italy Plant', NOW(), 'ACTIVE'),
('Brazil', 'brplant@adidas.com', 'MF008', 'Adidas Brazil Plant', NOW(), 'ACTIVE'),
('Mexico', 'mxplant@adidas.com', 'MF009', 'Adidas Mexico Plant', NOW(), 'ACTIVE'),
('UK', 'ukplant@adidas.com', 'MF010', 'Adidas UK Plant', NOW(), 'ACTIVE'),
('South Korea', 'skplant@adidas.com', 'MF011', 'Adidas Korea Plant', NOW(), 'ACTIVE'),
('Spain', 'spainplant@adidas.com', 'MF012', 'Adidas Spain Plant', NOW(), 'ACTIVE'),
('France', 'franceplant@adidas.com', 'MF013', 'Adidas France Plant', NOW(), 'ACTIVE'),
('Russia', 'russiaplant@adidas.com', 'MF014', 'Adidas Russia Plant', NOW(), 'ACTIVE'),
('Turkey', 'turkeyplant@adidas.com', 'MF015', 'Adidas Turkey Plant', NOW(), 'ACTIVE'),
('Egypt', 'egyptplant@adidas.com', 'MF016', 'Adidas Egypt Plant', NOW(), 'ACTIVE'),
('Pakistan', 'pkplant@adidas.com', 'MF017', 'Adidas Pakistan Plant', NOW(), 'ACTIVE'),
('Indonesia', 'idplant@adidas.com', 'MF018', 'Adidas Indonesia Plant', NOW(), 'ACTIVE'),
('Malaysia', 'malaysiaplant@adidas.com', 'MF019', 'Adidas Malaysia Plant', NOW(), 'ACTIVE'),
('Thailand', 'thailandplant@adidas.com', 'MF020', 'Adidas Thailand Plant', NOW(), 'ACTIVE'),
('Colombia', 'colplant@adidas.com', 'MF021', 'Adidas Colombia Plant', NOW(), 'ACTIVE'),
('Argentina', 'argplant@adidas.com', 'MF022', 'Adidas Argentina Plant', NOW(), 'ACTIVE'),
('Chile', 'chileplant@adidas.com', 'MF023', 'Adidas Chile Plant', NOW(), 'ACTIVE'),
('South Africa', 'saftplant@adidas.com', 'MF024', 'Adidas South Africa Plant', NOW(), 'ACTIVE'),
('Australia', 'ausplant@adidas.com', 'MF025', 'Adidas Australia Plant', NOW(), 'ACTIVE'),
('New Zealand', 'nzplant@adidas.com', 'MF026', 'Adidas New Zealand Plant', NOW(), 'ACTIVE'),
('Philippines', 'phplant@adidas.com', 'MF027', 'Adidas Philippines Plant', NOW(), 'ACTIVE'),
('Morocco', 'moroccoplant@adidas.com', 'MF028', 'Adidas Morocco Plant', NOW(), 'ACTIVE'),
('Portugal', 'portugalplant@adidas.com', 'MF029', 'Adidas Portugal Plant', NOW(), 'ACTIVE'),
('Poland', 'polandplant@adidas.com', 'MF030', 'Adidas Poland Plant', NOW(), 'ACTIVE'),
('Hungary', 'hungaryplant@adidas.com', 'MF031', 'Adidas Hungary Plant', NOW(), 'ACTIVE'),
('Ukraine', 'ukraineplant@adidas.com', 'MF032', 'Adidas Ukraine Plant', NOW(), 'ACTIVE'),
('Romania', 'romaniaplant@adidas.com', 'MF033', 'Adidas Romania Plant', NOW(), 'ACTIVE'),
('Greece', 'greeceplant@adidas.com', 'MF034', 'Adidas Greece Plant', NOW(), 'ACTIVE'),
('Netherlands', 'netherlandsplant@adidas.com', 'MF035', 'Adidas Netherlands Plant', NOW(), 'ACTIVE'),
('Belgium', 'belgiumplant@adidas.com', 'MF036', 'Adidas Belgium Plant', NOW(), 'ACTIVE'),
('Sweden', 'swedenplant@adidas.com', 'MF037', 'Adidas Sweden Plant', NOW(), 'ACTIVE'),
('Norway', 'norwayplant@adidas.com', 'MF038', 'Adidas Norway Plant', NOW(), 'ACTIVE'),
('Denmark', 'denmarkplant@adidas.com', 'MF039', 'Adidas Denmark Plant', NOW(), 'ACTIVE'),
('Finland', 'finlandplant@adidas.com', 'MF040', 'Adidas Finland Plant', NOW(), 'ACTIVE'),
('Switzerland', 'swissplant@adidas.com', 'MF041', 'Adidas Switzerland Plant', NOW(), 'ACTIVE'),
('Austria', 'austriaplant@adidas.com', 'MF042', 'Adidas Austria Plant', NOW(), 'ACTIVE'),
('Czech Republic', 'czplant@adidas.com', 'MF043', 'Adidas Czech Republic Plant', NOW(), 'ACTIVE'),
('Slovakia', 'slovakiaplant@adidas.com', 'MF044', 'Adidas Slovakia Plant', NOW(), 'ACTIVE'),
('Serbia', 'serbiaplant@adidas.com', 'MF045', 'Adidas Serbia Plant', NOW(), 'ACTIVE'),
('Croatia', 'croatiaplant@adidas.com', 'MF046', 'Adidas Croatia Plant', NOW(), 'ACTIVE'),
('Slovenia', 'slovenia@adidas.com', 'MF047', 'Adidas Slovenia Plant', NOW(), 'ACTIVE'),
('Bulgaria', 'bulgariaplant@adidas.com', 'MF048', 'Adidas Bulgaria Plant', NOW(), 'ACTIVE'),
('Lithuania', 'lithuaniaplant@adidas.com', 'MF049', 'Adidas Lithuania Plant', NOW(), 'ACTIVE'),
('Latvia', 'latviaplant@adidas.com', 'MF050', 'Adidas Latvia Plant', NOW(), 'ACTIVE'),
('Estonia', 'estoniaplant@adidas.com', 'MF051', 'Adidas Estonia Plant', NOW(), 'ACTIVE'),
('Kazakhstan', 'kzplant@adidas.com', 'MF052', 'Adidas Kazakhstan Plant', NOW(), 'ACTIVE'),
('Uzbekistan', 'uzplant@adidas.com', 'MF053', 'Adidas Uzbekistan Plant', NOW(), 'ACTIVE'),
('Azerbaijan', 'azplant@adidas.com', 'MF054', 'Adidas Azerbaijan Plant', NOW(), 'ACTIVE'),
('Saudi Arabia', 'ksa@adidas.com', 'MF055', 'Adidas Saudi Arabia Plant', NOW(), 'ACTIVE'),
('UAE', 'uae@adidas.com', 'MF056', 'Adidas UAE Plant', NOW(), 'ACTIVE'),
('Qatar', 'qatarplant@adidas.com', 'MF057', 'Adidas Qatar Plant', NOW(), 'ACTIVE'),
('Kuwait', 'kuwaitplant@adidas.com', 'MF058', 'Adidas Kuwait Plant', NOW(), 'ACTIVE'),
('Oman', 'omanplant@adidas.com', 'MF059', 'Adidas Oman Plant', NOW(), 'ACTIVE'),
('Bahrain', 'bahrainplant@adidas.com', 'MF060', 'Adidas Bahrain Plant', NOW(), 'ACTIVE'),
('Israel', 'israelplant@adidas.com', 'MF061', 'Adidas Israel Plant', NOW(), 'ACTIVE'),
('Jordan', 'jordanplant@adidas.com', 'MF062', 'Adidas Jordan Plant', NOW(), 'ACTIVE'),
('Lebanon', 'lebanonplant@adidas.com', 'MF063', 'Adidas Lebanon Plant', NOW(), 'ACTIVE'),
('Iraq', 'iraqplant@adidas.com', 'MF064', 'Adidas Iraq Plant', NOW(), 'ACTIVE'),
('Syria', 'syria@adidas.com', 'MF065', 'Adidas Syria Plant', NOW(), 'ACTIVE'),
('Yemen', 'yemenplant@adidas.com', 'MF066', 'Adidas Yemen Plant', NOW(), 'ACTIVE'),
('Sudan', 'sudanplant@adidas.com', 'MF067', 'Adidas Sudan Plant', NOW(), 'ACTIVE'),
('Kenya', 'kenyaplant@adidas.com', 'MF068', 'Adidas Kenya Plant', NOW(), 'ACTIVE'),
('Nigeria', 'nigeriaplant@adidas.com', 'MF069', 'Adidas Nigeria Plant', NOW(), 'ACTIVE'),
('Ghana', 'ghanaplant@adidas.com', 'MF070', 'Adidas Ghana Plant', NOW(), 'ACTIVE'),
('Ivory Coast', 'ivoryplant@adidas.com', 'MF071', 'Adidas Ivory Coast Plant', NOW(), 'ACTIVE'),
('Senegal', 'senegalplant@adidas.com', 'MF072', 'Adidas Senegal Plant', NOW(), 'ACTIVE'),
('Mali', 'maliplant@adidas.com', 'MF073', 'Adidas Mali Plant', NOW(), 'ACTIVE'),
('Cameroon', 'cameroonplant@adidas.com', 'MF074', 'Adidas Cameroon Plant', NOW(), 'ACTIVE'),
('Ethiopia', 'ethiopiaplant@adidas.com', 'MF075', 'Adidas Ethiopia Plant', NOW(), 'ACTIVE'),
('Uganda', 'ugandaplant@adidas.com', 'MF076', 'Adidas Uganda Plant', NOW(), 'ACTIVE'),
('Tanzania', 'tanzaniaplant@adidas.com', 'MF077', 'Adidas Tanzania Plant', NOW(), 'ACTIVE'),
('Zimbabwe', 'zimplant@adidas.com', 'MF078', 'Adidas Zimbabwe Plant', NOW(), 'ACTIVE'),
('Zambia', 'zambiaplant@adidas.com', 'MF079', 'Adidas Zambia Plant', NOW(), 'ACTIVE'),
('Botswana', 'botswanaplant@adidas.com', 'MF080', 'Adidas Botswana Plant', NOW(), 'ACTIVE'),
('Mozambique', 'mozambiquepplant@adidas.com', 'MF081', 'Adidas Mozambique Plant', NOW(), 'ACTIVE'),
('Angola', 'angolaplant@adidas.com', 'MF082', 'Adidas Angola Plant', NOW(), 'ACTIVE'),
('Congo', 'congoplant@adidas.com', 'MF083', 'Adidas Congo Plant', NOW(), 'ACTIVE'),
('Namibia', 'namibiaplant@adidas.com', 'MF084', 'Adidas Namibia Plant', NOW(), 'ACTIVE'),
('Madagascar', 'madagascarplant@adidas.com', 'MF085', 'Adidas Madagascar Plant', NOW(), 'ACTIVE'),
('Malawi', 'malawiplant@adidas.com', 'MF086', 'Adidas Malawi Plant', NOW(), 'ACTIVE'),
('Rwanda', 'rwandaplant@adidas.com', 'MF087', 'Adidas Rwanda Plant', NOW(), 'ACTIVE'),
('Burundi', 'burundiplant@adidas.com', 'MF088', 'Adidas Burundi Plant', NOW(), 'ACTIVE'),
('Liberia', 'liberiaplant@adidas.com', 'MF089', 'Adidas Liberia Plant', NOW(), 'ACTIVE'),
('Sierra Leone', 'sierraplant@adidas.com', 'MF090', 'Adidas Sierra Leone Plant', NOW(), 'ACTIVE'),
('Togo', 'togoplant@adidas.com', 'MF091', 'Adidas Togo Plant', NOW(), 'ACTIVE'),
('Benin', 'beninplant@adidas.com', 'MF092', 'Adidas Benin Plant', NOW(), 'ACTIVE'),
('Burkina Faso', 'burkinaplant@adidas.com', 'MF093', 'Adidas Burkina Faso Plant', NOW(), 'ACTIVE'),
('Gabon', 'gabonplant@adidas.com', 'MF094', 'Adidas Gabon Plant', NOW(), 'ACTIVE'),
('Mauritania', 'mauriplant@adidas.com', 'MF095', 'Adidas Mauritania Plant', NOW(), 'ACTIVE'),
('Guinea', 'guineaplant@adidas.com', 'MF096', 'Adidas Guinea Plant', NOW(), 'ACTIVE'),
('Central African Republic', 'carplant@adidas.com', 'MF097', 'Adidas Central African Republic Plant', NOW(), 'ACTIVE'),
('Equatorial Guinea', 'eqguineaplant@adidas.com', 'MF098', 'Adidas Equatorial Guinea Plant', NOW(), 'ACTIVE'),
('Swaziland', 'swazilandplant@adidas.com', 'MF099', 'Adidas Swaziland Plant', NOW(), 'ACTIVE'),
('Lesotho', 'lesothoplant@adidas.com', 'MF100', 'Adidas Lesotho Plant', NOW(), 'ACTIVE');
-- Dummy data for Item

INSERT INTO item (item_cd, item_nm, unit, unit_price, size, color, category, item_status, created_at, modified_at) VALUES
('AD001', 'Adidas Ultraboost', 'Pair', 180.00, 42, 'Black', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD002', 'Adidas T-shirt', 'Piece', 30.00, 38, 'White', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD003', 'Adidas NMD', 'Pair', 160.00, 44, 'Red', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD004', 'Adidas Hoodie', 'Piece', 60.00, 40, 'Gray', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD005', 'Adidas Joggers', 'Piece', 45.00, 40, 'Blue', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD006', 'Adidas Cap', 'Piece', 25.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD007', 'Adidas Tracksuit', 'Piece', 75.00, 42, 'Navy', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD008', 'Adidas Slides', 'Pair', 35.00, 44, 'Blue', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD009', 'Adidas Backpack', 'Piece', 80.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD010', 'Adidas Gloves', 'Pair', 20.00, 1, 'Gray', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD011', 'Adidas Sandals', 'Pair', 25.00, 41, 'Green', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD012', 'Adidas Beanie', 'Piece', 20.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD013', 'Adidas Soccer Ball', 'Piece', 30.00, 1, 'White', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD014', 'Adidas Sweatpants', 'Piece', 50.00, 36, 'Gray', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD015', 'Adidas Polo Shirt', 'Piece', 35.00, 40, 'Black', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD016', 'Adidas Tennis Shoes', 'Pair', 120.00, 42, 'White', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD017', 'Adidas Basketball Jersey', 'Piece', 60.00, 44, 'Blue', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD018', 'Adidas Cycling Shorts', 'Piece', 40.00, 38, 'Black', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD019', 'Adidas Running Shoes', 'Pair', 140.00, 43, 'Red', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD020', 'Adidas Soccer Cleats', 'Pair', 220.00, 42, 'Black', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD021', 'Adidas Golf Hat', 'Piece', 25.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD022', 'Adidas Yoga Mat', 'Piece', 50.00, 1, 'Purple', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD023', 'Adidas Duffle Bag', 'Piece', 90.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD024', 'Adidas Running Cap', 'Piece', 20.00, 1, 'Blue', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD025', 'Adidas Sweatband', 'Piece', 10.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD026', 'Adidas Weightlifting Gloves', 'Pair', 30.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD027', 'Adidas Headphones', 'Piece', 100.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD028', 'Adidas Water Bottle', 'Piece', 20.00, 1, 'Silver', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD029', 'Adidas Ankle Socks', 'Pair', 15.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD030', 'Adidas Leggings', 'Piece', 40.00, 38, 'Black', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD031', 'Adidas Training Shorts', 'Piece', 35.00, 36, 'Navy', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD032', 'Adidas Track Jacket', 'Piece', 60.00, 42, 'Red', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD033', 'Adidas Climbing Shoes', 'Pair', 130.00, 43, 'Gray', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD034', 'Adidas Volleyball', 'Piece', 25.00, 1, 'Yellow', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD035', 'Adidas Gym Bag', 'Piece', 70.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD036', 'Adidas Swim Cap', 'Piece', 15.00, 1, 'Blue', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD037', 'Adidas Hoodie', 'Piece', 60.00, 40, 'Gray', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD038', 'Adidas Baseball Cap', 'Piece', 25.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD039', 'Adidas Sunglasses', 'Piece', 120.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD040', 'Adidas Belt', 'Piece', 30.00, 1, 'Brown', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD041', 'Adidas Sandals', 'Pair', 50.00, 43, 'White', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD042', 'Adidas Baseball Glove', 'Piece', 150.00, 1, 'Brown', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD043', 'Adidas Shin Guards', 'Pair', 25.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD044', 'Adidas Tennis Racket', 'Piece', 200.00, 1, 'Red', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD045', 'Adidas Boxing Gloves', 'Pair', 75.00, 1, 'Black', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD046', 'Adidas Golf Gloves', 'Pair', 40.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD047', 'Adidas Bike Helmet', 'Piece', 85.00, 1, 'Blue', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD048', 'Adidas Training Vest', 'Piece', 55.00, 40, 'Green', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD049', 'Adidas Softball Bat', 'Piece', 100.00, 1, 'Silver', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD050', 'Adidas Weight Belt', 'Piece', 45.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD051', 'Adidas Yoga Pants', 'Piece', 60.00, 38, 'Gray', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD052', 'Adidas Track Shoes', 'Pair', 150.00, 42, 'Black', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD053', 'Adidas Tennis Bag', 'Piece', 85.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD054', 'Adidas Boxing Shorts', 'Piece', 45.00, 44, 'Blue', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD055', 'Adidas Sports Watch', 'Piece', 250.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD056', 'Adidas Windbreaker', 'Piece', 120.00, 42, 'Green', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD057', 'Adidas Running Cap', 'Piece', 30.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD058', 'Adidas Water Shoes', 'Pair', 60.00, 43, 'Blue', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD059', 'Adidas Rugby Jersey', 'Piece', 70.00, 44, 'Red', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD060', 'Adidas Rugby Ball', 'Piece', 40.00, 1, 'White', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD061', 'Adidas Shooting Sleeves', 'Pair', 20.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD062', 'Adidas Winter Gloves', 'Pair', 40.00, 1, 'Gray', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD063', 'Adidas Compression Socks', 'Pair', 30.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD064', 'Adidas Swim Shorts', 'Piece', 50.00, 40, 'Blue', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD065', 'Adidas Running Vest', 'Piece', 40.00, 38, 'Green', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD066', 'Adidas Beanie Hat', 'Piece', 20.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD067', 'Adidas Stretch Band', 'Piece', 15.00, 1, 'Gray', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD068', 'Adidas Duffel Bag', 'Piece', 90.00, 1, 'Red', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD069', 'Adidas Training Mask', 'Piece', 35.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD070', 'Adidas Boxing Headgear', 'Piece', 120.00, 1, 'Blue', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD071', 'Adidas Hiking Shoes', 'Pair', 160.00, 42, 'Brown', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD072', 'Adidas Hiking Backpack', 'Piece', 180.00, 1, 'Green', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD073', 'Adidas Climbing Harness', 'Piece', 110.00, 1, 'Gray', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD074', 'Adidas Mountain Bike', 'Piece', 1200.00, 1, 'Red', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD075', 'Adidas Ski Jacket', 'Piece', 300.00, 42, 'White', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD076', 'Adidas Snow Boots', 'Pair', 250.00, 43, 'Black', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD077', 'Adidas Ski Goggles', 'Piece', 100.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD078', 'Adidas Snow Pants', 'Piece', 150.00, 44, 'Blue', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD079', 'Adidas Winter Scarf', 'Piece', 40.00, 1, 'Gray', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD080', 'Adidas Skateboard', 'Piece', 180.00, 1, 'Black', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD081', 'Adidas BMX Bike', 'Piece', 500.00, 1, 'Red', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD082', 'Adidas Fitness Tracker', 'Piece', 250.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD083', 'Adidas Winter Coat', 'Piece', 400.00, 42, 'Black', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD084', 'Adidas Climbing Helmet', 'Piece', 90.00, 1, 'White', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD085', 'Adidas Ski Gloves', 'Pair', 50.00, 1, 'Blue', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD086', 'Adidas Snowboard', 'Piece', 450.00, 1, 'Black', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD087', 'Adidas Gymnastics Mat', 'Piece', 300.00, 1, 'Blue', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD088', 'Adidas Boxing Bag', 'Piece', 200.00, 1, 'Red', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD089', 'Adidas Judo Gi', 'Piece', 150.00, 44, 'White', 'Apparel', 'ON_SALE', NOW(), NOW()),
('AD090', 'Adidas Wrestling Shoes', 'Pair', 100.00, 42, 'Black', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD091', 'Adidas Martial Arts Belt', 'Piece', 15.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD092', 'Adidas Weightlifting Belt', 'Piece', 60.00, 1, 'Brown', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD093', 'Adidas Speed Rope', 'Piece', 25.00, 1, 'Blue', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD094', 'Adidas Resistance Band', 'Piece', 30.00, 1, 'Red', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD095', 'Adidas Kayak', 'Piece', 500.00, 1, 'Yellow', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD096', 'Adidas Canoe Paddle', 'Piece', 100.00, 1, 'Black', 'Accessories', 'ON_SALE', NOW(), NOW()),
('AD097', 'Adidas Basketball Shoes', 'Pair', 150.00, 44, 'White', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD098', 'Adidas Crossfit Shoes', 'Pair', 130.00, 42, 'Black', 'Footwear', 'ON_SALE', NOW(), NOW()),
('AD099', 'Adidas Lacrosse Stick', 'Piece', 250.00, 1, 'Blue', 'Sports', 'ON_SALE', NOW(), NOW()),
('AD100', 'Adidas Kayak Helmet', 'Piece', 80.00, 1, 'Red', 'Accessories', 'ON_SALE', NOW(), NOW());


-- Dummy data for ItemManufacture
INSERT INTO item_manufacture (unit_price, qty, created_at, mf_id, item_id) VALUES
(150.00, 500, NOW(), 1, 1),
(130.00, 300, NOW(), 2, 2),
(160.00, 200, NOW(), 3, 3),
(120.00, 400, NOW(), 4, 4),
(180.00, 600, NOW(), 5, 5),
(110.00, 450, NOW(), 6, 6),
(145.00, 250, NOW(), 7, 7),
(155.00, 350, NOW(), 8, 8),
(170.00, 500, NOW(), 9, 9),
(135.00, 380, NOW(), 10, 10),
(200.00, 650, NOW(), 11, 11),
(190.00, 700, NOW(), 12, 12),
(175.00, 300, NOW(), 13, 13),
(165.00, 550, NOW(), 14, 14),
(180.00, 700, NOW(), 15, 15),
(145.00, 360, NOW(), 16, 16),
(135.00, 410, NOW(), 17, 17),
(150.00, 500, NOW(), 18, 18),
(170.00, 400, NOW(), 19, 19),
(160.00, 600, NOW(), 20, 20);

-- Dummy data for Buyer
INSERT INTO buyer (buyer_cd, email, buyer_nm, tel, address, business_type, buyer_status) VALUES
('B001', 'buyer1@store.com', 'Buyer 1', '123-456-7890', '123 Main St', 'Retail', 'ACTIVE'),
('B002', 'buyer2@store.com', 'Buyer 2', '987-654-3210', '456 Oak St', 'Wholesale', 'ACTIVE'),
('B003', 'buyer3@store.com', 'Buyer 3', '555-789-1234', '789 Pine St', 'Retail', 'ACTIVE'),
('B004', 'buyer4@store.com', 'Buyer 4', '555-123-4567', '123 Elm St', 'Wholesale', 'ACTIVE'),
('B005', 'buyer5@store.com', 'Buyer 5', '444-555-6666', '456 Cedar St', 'Retail', 'ACTIVE'),
('B006', 'buyer6@store.com', 'Buyer 6', '333-444-5555', '789 Maple St', 'E-commerce', 'ACTIVE'),
('B007', 'buyer7@store.com', 'Buyer 7', '555-333-2222', '101 Oak St', 'Retail', 'ACTIVE'),
('B008', 'buyer8@store.com', 'Buyer 8', '555-666-7777', '222 Maple St', 'Retail', 'ACTIVE'),
('B009', 'buyer9@store.com', 'Buyer 9', '555-888-9999', '789 Cedar St', 'E-commerce', 'ACTIVE'),
('B010', 'buyer10@store.com', 'Buyer 10', '444-999-0000', '123 Birch St', 'Distributor', 'ACTIVE'),
('B011', 'buyer11@store.com', 'Buyer 11', '123-987-6543', '111 Maple St', 'Retail', 'ACTIVE'),
('B012', 'buyer12@store.com', 'Buyer 12', '444-321-1234', '222 Elm St', 'Wholesale', 'ACTIVE'),
('B013', 'buyer13@store.com', 'Buyer 13', '555-234-5678', '333 Cedar St', 'Retail', 'ACTIVE'),
('B014', 'buyer14@store.com', 'Buyer 14', '666-777-8888', '444 Pine St', 'E-commerce', 'ACTIVE'),
('B015', 'buyer15@store.com', 'Buyer 15', '123-456-9876', '555 Oak St', 'Retail', 'ACTIVE'),
('B016', 'buyer16@store.com', 'Buyer 16', '789-123-4567', '666 Birch St', 'Retail', 'ACTIVE'),
('B017', 'buyer17@store.com', 'Buyer 17', '987-654-3333', '777 Maple St', 'Wholesale', 'ACTIVE'),
('B018', 'buyer18@store.com', 'Buyer 18', '321-555-9876', '888 Cedar St', 'Distributor', 'ACTIVE'),
('B019', 'buyer19@store.com', 'Buyer 19', '654-987-3210', '999 Pine St', 'Retail', 'ACTIVE'),
('B020', 'buyer20@store.com', 'Buyer 20', '333-444-9876', '101 Oak St', 'Wholesale', 'ACTIVE');

-- Dummy data for BuyerItem
INSERT INTO buyer_item (unit_price, start_date, end_date, buyer_id, item_id) VALUES
(175.00, '2020-01-01', '2021-01-01', 1, 61),
(160.00, '2020-02-01', '2021-02-01', 2, 62),
(140.00, '2020-03-01', '2021-03-01', 3, 63),
(120.00, '2020-04-01', '2021-04-01', 4, 64),
(180.00, '2020-05-01', '2021-05-01', 5, 65),
(145.00, '2020-06-01', '2021-06-01', 6, 66),
(165.00, '2020-07-01', '2021-07-01', 7, 67),
(150.00, '2020-08-01', '2021-08-01', 8, 68),
(170.00, '2020-09-01', '2021-09-01', 9, 69),
(130.00, '2020-10-01', '2021-10-01', 10, 70),
(155.00, '2020-11-01', '2021-11-01', 11, 71),
(170.00, '2020-12-01', '2021-12-01', 12, 72),
(165.00, '2020-01-01', '2021-01-01', 13, 73),
(150.00, '2020-02-01', '2021-02-01', 14, 74),
(145.00, '2020-03-01', '2021-03-01', 15, 75),
(135.00, '2020-04-01', '2021-04-01', 16, 76),
(180.00, '2020-05-01', '2021-05-01', 17, 77),
(125.00, '2020-06-01', '2021-06-01', 18, 78),
(120.00, '2020-07-01', '2021-07-01', 19, 79),
(110.00, '2020-08-01', '2021-08-01', 20, 80),
(175.00, '2021-01-01', '2022-01-01', 9, 49),
(160.00, '2021-02-01', '2022-02-01', 10, 50),
(140.00, '2021-03-01', '2022-03-01', 11, 51),
(120.00, '2021-04-01', '2022-04-01', 12, 52),
(180.00, '2021-05-01', '2022-05-01', 13, 53),
(145.00, '2021-06-01', '2022-06-01', 14, 54),
(165.00, '2021-07-01', '2022-07-01', 15, 55),
(150.00, '2021-08-01', '2022-08-01', 16, 56),
(170.00, '2021-09-01', '2022-09-01', 17, 57),
(130.00, '2021-10-01', '2022-10-01', 18, 58),
(155.00, '2021-11-01', '2022-11-01', 19, 59),
(170.00, '2021-12-01', '2022-12-01', 20, 60),
(175.00, '2023-11-05', '2024-11-04', 1, 1),
(160.00, '2023-11-05', '2024-11-04', 2, 2),
(140.00, '2023-11-05', '2024-11-04', 3, 3),
(120.00, '2023-11-05', '2024-11-04', 4, 4),
(180.00, '2023-11-05', '2024-11-04', 5, 5),
(145.00, '2023-11-05', '2024-11-04', 6, 6),
(165.00, '2023-11-05', '2024-11-04', 7, 7),
(150.00, '2023-11-05', '2024-11-04', 8, 8),
(170.00, '2023-11-05', '2024-11-04', 9, 9),
(130.00, '2023-11-05', '2024-11-04', 10, 10),
(155.00, '2023-11-05', '2024-11-04', 11, 11),
(170.00, '2023-11-05', '2024-11-04', 12, 12),
(165.00, '2023-11-05', '2024-11-04', 13, 13),
(150.00, '2023-11-05', '2024-11-04', 14, 14),
(145.00, '2023-11-05', '2024-11-04', 15, 15),
(135.00, '2023-11-05', '2024-11-04', 16, 16),
(180.00, '2023-11-05', '2024-11-04', 17, 17),
(125.00, '2023-11-05', '2024-11-04', 18, 18),
(120.00, '2023-11-05', '2024-11-04', 19, 19),
(110.00, '2023-11-05', '2024-11-04', 20, 20);