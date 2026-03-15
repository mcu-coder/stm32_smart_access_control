#include "JR6001.h"
#include "usart.h"
#include "git.h"
#include "delay.h"
#include "stdarg.h"
#include "stdio.h"
#include "string.h"
// 延时函数重定义 JR6001延时函数定义，用户自行更改为自己的延时函数
#define JR6001_ms(ms) delay_ms(ms)
#define JR6001_us(us) delay_us(us)

void JR6001_Volumecontrol(u8 num);

void JR6001_Init(void)
{
	// GPIO端口设置
	GPIO_InitTypeDef GPIO_InitStructure;

#ifdef JR6001_BUSY // 查忙引脚配置

	RCC_APB2PeriphClockCmd(JR6001_BUSY_GPIO_CLK, ENABLE); // 使能POR时钟
	GPIO_InitStructure.GPIO_Pin = JR6001_BUSY_GPIO_PIN;
	GPIO_InitStructure.GPIO_Mode = JR6001_BUSY_GPIO_Mode;
	GPIO_Init(JR6001_BUSY_GPIO_PORT, &GPIO_InitStructure);

#endif
	// 已关闭
	JR6001_SongControl(1,0);
	JR6001_ms(300);
	JR6001_Volumecontrol(31); // 设置音量
	JR6001_ms(100);
	JR6001_Volumecontrol(31); // 设置音量
	JR6001_ms(100);
	JR6001_Volumecontrol(31); // 设置音量
	JR6001_ms(100);
}

// 串口发送数据
void JR6001_SendCode(u8 *str, u8 busy)
{
	u16 len, i;
	len = strlen((char *)str);
	printf("%s\n", str);

	if (busy == 1 && JR6001_BUSY_IO == 0)
	{
		for (i = 0; i <= len; i++) // 循环发送数据
		{
			while (USART_GetFlagStatus(USART1, USART_FLAG_TC) == RESET)
				; // 循环发送,直到发送完毕
			USART_SendData(USART1, str[i]);
		}
		// 提示次数
//		if(device_state_init.speek_time){
//			device_state_init.speek_time--;
//		}
	}
	else if (busy == 0)
	{
		Usart_SendString(USART1,(char*)str);
//		for (i = 0; i <= len; i++) // 循环发送数据
//		{
//			while (USART_GetFlagStatus(USART1, USART_FLAG_TC) == RESET)
//				; // 循环发送,直到发送完毕
//			USART_SendData(USART1, str[i]);
//		}
	}
	JR6001_ms(10);
}
// 基础指令发送
void JR6001_Instruction(u8 *str, u8 busy)
{
	u8 JR6001_Buff[20];
	sprintf((char *)JR6001_Buff, "%s\r\n", str);
	JR6001_SendCode(JR6001_Buff, busy);
}
// 曲目选择
void JR6001_SongControl(u8 num, u8 busy)
{
	u8 JR6001_Buff[20];
	if (num < 10)
	{
		sprintf((char *)JR6001_Buff, "A7:0000%d\r\n", num);
		JR6001_SendCode(JR6001_Buff, busy);
	}
	else if (num < 100)
	{
		sprintf((char *)JR6001_Buff, "A7:000%d\r\n", num);
		JR6001_SendCode(JR6001_Buff, busy);
	}
	else
	{
		sprintf((char *)JR6001_Buff, "A7:00%d\r\n", num);
		JR6001_SendCode(JR6001_Buff, busy);
	}
}
// 音量选择
void JR6001_Volumecontrol(u8 num)
{
	u8 JR6001_Buff[20];
	sprintf((char *)JR6001_Buff, "AF:%d\r\n", num);
	JR6001_SendCode(JR6001_Buff, 0);
}

/*
	JR6001_Init();
	JR6001_SongControl(5,0);

*/
