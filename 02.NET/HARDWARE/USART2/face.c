#include "face.h"
#include "usart.h"
#include "usart2.h"
#include "led.h"
#include "relay.h"
#include "delay.h"
//  C库
#include "cjson.h"
#include <string.h>
#include <stdio.h>

extern u8 USART2_RX_BUF[UART2_RXBUF_SIZE]; // 接收缓冲,最大USART3_MAX_RECV_LEN个字节.
extern U16 Uart2_RecvCount;				   // 串口接收计数
// 处理人脸接收到的数据（hex）
void massage_face_hex(char *message)
{
	if (Uart2_RecvCount)
	{
		switch (USART2_RX_BUF[7])
		{
		case 0x12: // 识别成功
			if (USART2_RX_BUF[8] == 0x00)
			{
				// 给app发送信息
				if(	Data_init.face_id != USART2_RX_BUF[10] + 1){
						Data_init.face_id = USART2_RX_BUF[10] + 1;
						Data_init.face_id_copy = Data_init.face_id;
				
						// 主动发送
						if(device_state_init.check_open<3){
							Data_init.App=1;
							device_state_init.BeepOpenTime = 50;
							
							OLED_Clear();						
							device_state_init.check_open =device_state_init.Door_Time;
							device_state_init.open =1;
							device_state_init.open_state = 2;
							Data_init.Page = 6;
							OLED_ShowCH(16, 6, "       K#关闭");
						}
						
				}
			
			}
			else
			{
				Data_init.face_id = 0;
			}

			break;
		case 0x13: // 注册
			if (USART2_RX_BUF[8] == 0x00)
			{
				Data_init.face_id = USART2_RX_BUF[10] + 1;
				Data_init.register_id = Data_init.face_id;
				//printf("\r\n注册ID:%d", Data_init.face_id);
				// 蜂鸣器叫两声
				device_state_init.BeepOpenTime = 800;
				Data_init.App = 2;
			}
			else if (USART2_RX_BUF[8] == 0x09)
			{
				Data_init.register_id = 0; // 清空id
			}
			else
			{
				Data_init.register_id = 0; // 清空id
			}

			break;
		case 0x20: // 删除
			if (USART2_RX_BUF[8] == 0x00)
			{

				//printf("\r\n删除成功");
				device_state_init.BeepOpenTime = 250;
				Data_init.App = 4;
			}
			else
			{
				Data_init.delete_id = 0; // 清空id
			}
			break;
		default:
			break;
		}
		// printf("\r\n");
		// for (i = 0; i < Uart2_RecvCount; i++)
		// {
		// 	printf("%02X ", USART2_RX_BUF[i]);
		// }
	}


}
// 8位的和效验
U8 SumCheck(U8 *Dat, U8 Len)
{
	U8 Sum = 0;
	U8 i;
	for (i = 0; i < Len; i++)
	{
		Sum += Dat[i];
	}

	return Sum;
}
// 检测人脸指令
void check_face_id(void)
{
	U8 check_face_cmd[8] = {0xEF, 0xAA, 0x12, 0x00, 0x00, 0x00, 0x00, 00};
	check_face_cmd[7] = SumCheck(check_face_cmd + 2, 6);
	Usart_SendArray(USART_2, (char *)check_face_cmd, 8);
}
// 注册人脸指令
void register_face_id(void)
{
	U8 check_face_cmd[8] = {0xEF, 0xAA, 0x13, 0x00, 0x00, 0x00, 0x00, 00};
	check_face_cmd[7] = SumCheck(check_face_cmd + 2, 6);
	Usart_SendArray(USART_2, (char *)check_face_cmd, 8);
}
// 删除指定人脸指令
void delete_face_id(U16 ID)
{
	U8 check_face_cmd[10] = {0xEF, 0xAA, 0x20, 0x00, 0x00, 0x00, 0x02, 0, 0, 0};
	// EF AA 20 00 00 00 02 00 01 23
	// EFAA20000000 02 00 00 22

	if (ID <= 255)
	{
		check_face_cmd[8] = ID;
	}
	check_face_cmd[9] = SumCheck(check_face_cmd + 2, 8);
	Usart_SendArray(USART_2, (char *)check_face_cmd, 10);
}
// 删除指定人脸指令
void deleteall_face_id(void)
{
	U8 check_face_cmd[8] = {0xEF, 0xAA, 0x21, 0x00, 0x00, 0x00, 0x00, 0};
	check_face_cmd[7] = SumCheck(check_face_cmd + 2, 6);
	Usart_SendArray(USART_2, (char *)check_face_cmd, 8);
}
