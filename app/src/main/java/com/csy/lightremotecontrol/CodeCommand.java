package com.csy.lightremotecontrol;

public class CodeCommand {
    //编码规则
    //起始码S电平宽度为：9000us低电平+4500us高电平
    public static final int startdown = 9000;
    public static final int startup = 4500;

    //数据码由0，1组成：
    //0的电平宽度为：600us低电平+600us高电平，
    public static final int bit0Down = 600;
    public static final int bit0Up = 600;

    //1的电平宽度为：600us低电平+1600us高电平
    public static final int bit1Down = 600;
    public static final int bit1Up = 1600;


    //小房子命令
    public static int[] CodeBalcony = new int[36];
    public static int[] codeBedroom = new int[36];
    public static int[] CodeSmallLivingRoom = new int[36];
    public static int[] CodeLivingRoom = new int[36];
    public static int[] CodeLeftLawn = new int[36];
    public static int[] CodeRightLawn = new int[36];
    public static int[] CodeBrighter = new int[36];
    public static int[] CodeDarker = new int[36];

    //大房子命令
    public static int[] Open = new int[36];
    public static int[] Close = new int[36];


    //命令格式（数组内的数值拼接）
    //起始码+8位数据码+8位校验码+1位结束码（写0）。校验码直接取数据的反码
    //先发送低位
    static {
        FillCode8(CodeBalcony, 0x11);
        FillCode8(codeBedroom, 0x12);
        FillCode8(CodeSmallLivingRoom, 0x13);
        FillCode8(CodeLivingRoom, 0x14);
        FillCode8(CodeLeftLawn, 0x15);
        FillCode8(CodeRightLawn, 0x16);
        FillCode8(CodeBrighter, 0x21);
        FillCode8(CodeDarker, 0x22);

        FillCode8(Open, 0x31);
        FillCode8(Open, 0x32);
    }

    //发送8字节的信息
    private static void FillCode8(int[] code, int value) {
        code[0] = startdown;
        code[1] = startup;
        for (int i = 0; i < 8; i++) {
            if ((value & 0x01) == 1) {
                code[2 + i * 2] = bit1Down;
                code[3 + i * 2] = bit1Up;
                code[18 + i * 2] = bit0Down;
                code[19 + i * 2] = bit0Up;
            } else {
                code[2 + i * 2] = bit0Down;
                code[3 + i * 2] = bit0Up;
                code[18 + i * 2] = bit1Down;
                code[19 + i * 2] = bit1Up;
            }
            value >>= 1;
        }
        code[34] = bit0Down;
        code[35] = bit0Up;
    }

    //发送16字节的信息
    private static void FillCode16(int[] code, int value) {
        code[0] = startdown;
        code[1] = startup;
        //前8字节的信息和校验码
        for (int i = 0; i < 8; i++) {
            if ((value & 0x01) == 1) {
                code[2 + i * 2] = bit1Down;
                code[3 + i * 2] = bit1Up;
                code[18 + i * 2] = bit0Down;
                code[19 + i * 2] = bit0Up;
            } else {
                code[2 + i * 2] = bit0Down;
                code[3 + i * 2] = bit0Up;
                code[18 + i * 2] = bit1Down;
                code[19 + i * 2] = bit1Up;
            }
            value >>= 1;
        }
        //后8字节的信息和校验码
        for (int i = 0; i < 8; i++) {
            if ((value & 0x01) == 1) {
                code[2 + 32 + i * 2] = bit1Down;
                code[3 + 32 + i * 2] = bit1Up;
                code[18 + 32 + i * 2] = bit0Down;
                code[19 + 32 + i * 2] = bit0Up;
            } else {
                code[2 + 32 + i * 2] = bit0Down;
                code[3 + 32 + i * 2] = bit0Up;
                code[18 + 32 + i * 2] = bit1Down;
                code[19 + 32 + i * 2] = bit1Up;
            }
            value >>= 1;
        }
        code[66] = bit0Down;
        code[67] = bit0Up;
    }

    //将percent+0x80即可
    public static int[] GetBrighter(int percent) {
        int[] array = new int[36];
        FillCode8(array, 128 + percent);
        return array;
    }
}