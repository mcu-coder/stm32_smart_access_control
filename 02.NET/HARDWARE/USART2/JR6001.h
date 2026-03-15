#ifndef __JR6001_H
#define __JR6001_H

#include "sys.h"

// 查忙使能
#define JR6001_BUSY 0 // 0.失能 1.使能

// JR6001查忙引脚配置 高电平忙
#define JR6001_BUSY_GPIO_CLK RCC_APB2Periph_GPIOA
#define JR6001_BUSY_GPIO_PORT GPIOA
#define JR6001_BUSY_GPIO_PIN GPIO_Pin_8
#define JR6001_BUSY_GPIO_Mode GPIO_Mode_IPU

#define JR6001_BUSY_IO GPIO_ReadInputDataBit(JR6001_BUSY_GPIO_PORT, JR6001_BUSY_GPIO_PIN) // 读取查忙引脚

// JR6001串口命令[播放 暂停 上一曲 下一曲 音量加 音量减]
#define Play "A2"
#define Suspend "A3"
#define On_Song "A5"
#define Next_Song "A6"
#define Volumeadd "B0"
#define Volumesub "B1"

void JR6001_Init(void);
// 串口发送数据
void JR6001_SendCode(u8 *str, u8 busy);
// 基础指令发送
void JR6001_Instruction(u8 *str, u8 busy);
// 曲目选择
void JR6001_SongControl(u8 num, u8 busy);
// 音量选择
void JR6001_Volumecontrol(u8 num);
#endif
