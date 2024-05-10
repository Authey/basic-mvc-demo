/*
 Navicat Premium Data Transfer

 Source Server         : BASIC_MVC
 Source Server Type    : Oracle
 Source Server Version : 190000 (Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production)
 Source Host           : localhost:1521
 Source Schema         : BASIC_MVC

 Target Server Type    : Oracle
 Target Server Version : 190000 (Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production)
 File Encoding         : 65001

 Date: 10/05/2024 17:08:28
*/


-- ----------------------------
-- Table structure for SYS_USER
-- ----------------------------
DROP TABLE "BASIC_MVC"."SYS_USER";
CREATE TABLE "BASIC_MVC"."SYS_USER" (
  "ID" VARCHAR2(50 BYTE) VISIBLE NOT NULL,
  "USERNAME" VARCHAR2(50 BYTE) VISIBLE NOT NULL,
  "PASSWORD" VARCHAR2(100 BYTE) VISIBLE,
  "AUTH_LEVEL" VARCHAR2(50 BYTE) VISIBLE
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 65536 
  NEXT 1048576 
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;
COMMENT ON COLUMN "BASIC_MVC"."SYS_USER"."ID" IS '本表主键';
COMMENT ON COLUMN "BASIC_MVC"."SYS_USER"."USERNAME" IS '用户名';
COMMENT ON COLUMN "BASIC_MVC"."SYS_USER"."PASSWORD" IS '密码';
COMMENT ON COLUMN "BASIC_MVC"."SYS_USER"."AUTH_LEVEL" IS '权限等级';

-- ----------------------------
-- Records of SYS_USER
-- ----------------------------
INSERT INTO "BASIC_MVC"."SYS_USER" VALUES ('5502CEE1-912A-4CE3-8B5C-00DD3B9F4A73', 'Autz', 'C5F6F584B79463F58C223F18FEF206EF', 'GUEST');
INSERT INTO "BASIC_MVC"."SYS_USER" VALUES ('F47B8F28-2685-4A1B-89BE-B2A702DFD419', 'Authey', '63A9F0EA7BB98050796B649E85481845', 'GUEST');

-- ----------------------------
-- Primary Key structure for table SYS_USER
-- ----------------------------
ALTER TABLE "BASIC_MVC"."SYS_USER" ADD CONSTRAINT "SYS_C007465" PRIMARY KEY ("ID");

-- ----------------------------
-- Uniques structure for table SYS_USER
-- ----------------------------
ALTER TABLE "BASIC_MVC"."SYS_USER" ADD CONSTRAINT "SYS_C007482" UNIQUE ("USERNAME") NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Checks structure for table SYS_USER
-- ----------------------------
ALTER TABLE "BASIC_MVC"."SYS_USER" ADD CONSTRAINT "SYS_C007464" CHECK ("ID" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "BASIC_MVC"."SYS_USER" ADD CONSTRAINT "SYS_C007483" CHECK ("USERNAME" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
