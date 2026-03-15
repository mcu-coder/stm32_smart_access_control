#include "relay.h"
#include "delay.h"
void RELAY_GPIO_Config(void)
{

	GPIO_InitTypeDef GPIO_InitStructure;

	RCC_APB2PeriphClockCmd(BEEP_GPIO_CLK , ENABLE);

// GPIO_InitStructure.GPIO_Pin = RELAY1_GPIO_PIN;
// GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
// GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
// GPIO_Init(RELAY1_GPIO_PORT, &GPIO_InitStructure);

//	 GPIO_InitStructure.GPIO_Pin = RELAY2_GPIO_PIN;
//	 GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
//	 GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
//	 GPIO_Init(RELAY2_GPIO_PORT, &GPIO_InitStructure);

	GPIO_InitStructure.GPIO_Pin = BEEP_GPIO_PIN;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_Init(BEEP_GPIO_PORT, &GPIO_InitStructure);
	GPIO_ResetBits(BEEP_GPIO_PORT, BEEP_GPIO_PIN);

	// GPIO_SetBits(RELAY1_GPIO_PORT, RELAY1_GPIO_PIN);
	// GPIO_SetBits(RELAY2_GPIO_PORT, RELAY2_GPIO_PIN);
}
void Beep_time(u16 time)
{
		BeepOnt = 1;
		delay_ms(time);
		BeepOnt = 0;
}
