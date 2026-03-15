#ifndef __MAX30102_H
#define __MAX30102_H
#include "sys.h"
//////////////////////////////////////////////////////////////////////////////////

#define IN_GPIO_PORT GPIOB               /* GPIO端口 */
#define IN_GPIO_CLK RCC_APB2Periph_GPIOB /* GPIO端口时钟 */
#define IN_GPIO_PIN GPIO_Pin_7           /* 连接到SCL时钟线的GPIO */

#define SCLK__GPIO_PORT GPIOB              /* GPIO端口 */
#define SCLK_GPIO_CLK RCC_APB2Periph_GPIOB /* GPIO端口时钟 */
#define SCLK__GPIO_PIN GPIO_Pin_9          /* 连接到SCL时钟线的GPIO */

#define DATA__GPIO_PORT GPIOB              /* GPIO端口 */
#define DATA_GPIO_CLK RCC_APB2Periph_GPIOB /* GPIO端口时钟 */
#define DATA__GPIO_PIN GPIO_Pin_8          /* 连接到SCL时钟线的GPIO */

// IO操作函数
#define Heart_IIC_SCL PBout(9) // SCL
#define Heart_IIC_SDA PBout(8) // SDA
#define Heart_READ_SDA PBin(8) // 输入SDA

// IIC所有操作函数
void heart_IIC_Init(void);                 // 初始化IIC的IO口
void heart_IIC_Start(void);                // 发送IIC开始信号
void heart_IIC_Stop(void);                 // 发送IIC停止信号
void heart_IIC_Send_Byte(u8 txd);          // IIC发送一个字节
u8 heart_IIC_Read_Byte(unsigned char ack); // IIC读取一个字节
u8 heart_IIC_Wait_Ack(void);               // IIC等待ACK信号
void heart_IIC_Ack(void);                  // IIC发送ACK信号
void heart_IIC_NAck(void);                 // IIC不发送ACK信号

void heart_IIC_Write_One_Byte(u8 daddr, u8 addr, u8 data);
void heart_IIC_Read_One_Byte(u8 daddr, u8 addr, u8 *data);

void IIC_WriteBytes(u8 WriteAddr, u8 *data, u8 dataLength);
void IIC_ReadBytes(u8 deviceAddr, u8 writeAddr, u8 *data, u8 dataLength);


#endif
