// 网络设备
#include "esp8266.h"

// 协议文件
#include "aly.h"
#include "mqttkit.h"
#include "delay.h"

// 硬件驱动
#include "usart.h"

//  C库
#include <string.h>
#include <stdio.h>

extern unsigned char esp8266_buf[256];

U8 Connect_Net;



unsigned char MQTT_FillBuf(char *buf)
{
	char text[256];
	memset(text, 0, sizeof(text));

	strcpy(buf, "{");


	// 密码
	if( device_state_init.DoorPwd!=0 ){
		memset(text, 0, sizeof(text));
		sprintf(text, "\"pwd\":\"%d\",", device_state_init.DoorPwd);
		strcat(buf, text);
	}
	
	
	memset(text, 0, sizeof(text));
	sprintf(text, "\"door\":\"%d\",", device_state_init.open);
	strcat(buf, text);
	
	memset(text, 0, sizeof(text));
	sprintf(text, "\"door_time\":\"%d\"", device_state_init.Door_Time); 
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "}");
	strcat(buf, text);

	return strlen(buf);
}

unsigned char MQTT_fid_Info(char *buf)
{
	char text[256];
	memset(text, 0, sizeof(text));

	strcpy(buf, "{");

	memset(text, 0, sizeof(text));
	sprintf(text, "\"fid\":\"%d\"", Data_init.face_id); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "}");
	strcat(buf, text);

	return strlen(buf);
}
unsigned char MQTT_did_Info(char *buf)
{
	char text[256];
	memset(text, 0, sizeof(text));

	strcpy(buf, "{");

	memset(text, 0, sizeof(text));
	sprintf(text, "\"did\":\"%d\"", Data_init.delete_id); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "}");
	strcat(buf, text);

	return strlen(buf);
}
unsigned char MQTT_frfid_Info(char *buf)
{
	char text[256];
	memset(text, 0, sizeof(text));
	strcpy(buf, "{");

	memset(text, 0, sizeof(text));
	sprintf(text, "\"handid\":\"%d\"", Data_init.hand_id); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "}");
	strcat(buf, text);

	return strlen(buf);
}
//==========================================================
//	函数名称：	Send
//==========================================================
void SendMqtt(U8 Cmd)
{
	char buf[254];
	short body_len = 0;
	memset(buf, 0, sizeof(buf));
	switch (Cmd)
	{
	case 1:
		body_len = MQTT_FillBuf(buf); // 数据流
		break;
	case 2:
		body_len = MQTT_fid_Info(buf); // 数据流
		break;
	case 3:
		body_len = MQTT_frfid_Info(buf); // 数据流
		break;
	case 4:
		body_len = MQTT_did_Info(buf); // 数据流
		break;
	default:
		break;
	}

	if (body_len)
	{
		// 封包
		Usart_SendString(USART3, buf);
		delay_ms(50);
	}
}


