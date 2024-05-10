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

 Date: 10/05/2024 17:08:41
*/


-- ----------------------------
-- Table structure for TEST
-- ----------------------------
DROP TABLE "BASIC_MVC"."TEST";
CREATE TABLE "BASIC_MVC"."TEST" (
  "ID" VARCHAR2(50 BYTE) VISIBLE NOT NULL,
  "CREATE_DATE" DATE VISIBLE,
  "FLAG" VARCHAR2(1 BYTE) VISIBLE,
  "DESCRIPTION" VARCHAR2(255 BYTE) VISIBLE
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
COMMENT ON COLUMN "BASIC_MVC"."TEST"."ID" IS '主键';
COMMENT ON COLUMN "BASIC_MVC"."TEST"."CREATE_DATE" IS '创建时间';
COMMENT ON COLUMN "BASIC_MVC"."TEST"."FLAG" IS '标志位';
COMMENT ON COLUMN "BASIC_MVC"."TEST"."DESCRIPTION" IS '描述';

-- ----------------------------
-- Records of TEST
-- ----------------------------
INSERT INTO "BASIC_MVC"."TEST" VALUES ('1EFC535F-34E0-4271-B43A-71E462539D29', TO_DATE('2024-05-09 15:40:24', 'SYYYY-MM-DD HH24:MI:SS'), '1', 'This Data Is For JDBC Test Only');
INSERT INTO "BASIC_MVC"."TEST" VALUES ('0836F4F3-2974-48C7-8CDC-7AA3F53956A1', TO_DATE('2024-05-09 15:40:26', 'SYYYY-MM-DD HH24:MI:SS'), '0', 'This Data Is For JDBC Test Only');

-- ----------------------------
-- Primary Key structure for table TEST
-- ----------------------------
ALTER TABLE "BASIC_MVC"."TEST" ADD CONSTRAINT "SYS_C007477" PRIMARY KEY ("ID");

-- ----------------------------
-- Checks structure for table TEST
-- ----------------------------
ALTER TABLE "BASIC_MVC"."TEST" ADD CONSTRAINT "SYS_C007476" CHECK ("ID" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
