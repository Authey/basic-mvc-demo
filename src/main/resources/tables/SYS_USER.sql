/*
 Navicat Premium Data Transfer

 Source Server         : BASIC_MVC
 Source Server Type    : Oracle
 Source Server Version : 190000
 Source Host           : localhost:1521
 Source Schema         : BASIC_MVC

 Target Server Type    : Oracle
 Target Server Version : 190000
 File Encoding         : 65001

 Date: 06/05/2024 19:41:16
*/


-- ----------------------------
-- Table structure for SYS_USER
-- ----------------------------
DROP TABLE "BASIC_MVC"."SYS_USER";
CREATE TABLE "BASIC_MVC"."SYS_USER" (
  "ID" VARCHAR2(50 BYTE) VISIBLE NOT NULL,
  "USERNAME" VARCHAR2(50 BYTE) VISIBLE,
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
INSERT INTO "BASIC_MVC"."SYS_USER" VALUES ('8dbca3f8-5ceb-46b8-afee-89faccc64fdd', 'Authey', 'root', 'ALL');

-- ----------------------------
-- Primary Key structure for table SYS_USER
-- ----------------------------
ALTER TABLE "BASIC_MVC"."SYS_USER" ADD CONSTRAINT "SYS_C007465" PRIMARY KEY ("ID");

-- ----------------------------
-- Checks structure for table SYS_USER
-- ----------------------------
ALTER TABLE "BASIC_MVC"."SYS_USER" ADD CONSTRAINT "SYS_C007464" CHECK ("ID" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
