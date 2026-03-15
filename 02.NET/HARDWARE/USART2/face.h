#ifndef __FACE_H
#define __FACE_H

#include "stm32f10x.h"
#include "git.h"

// 处理人脸接收到的数据（hex）
void massage_face_hex(char *message);
// 检测人脸指令
void check_face_id(void);
// 注册人脸指令
void register_face_id(void);

// 删除指定人脸指令
void delete_face_id(U16 ID);
// 删除全部人脸指令
void deleteall_face_id(void);

#endif /* __FACE_H */
