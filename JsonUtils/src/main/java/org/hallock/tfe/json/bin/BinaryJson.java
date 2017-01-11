package org.hallock.tfe.json.bin;

public interface BinaryJson
{
	/* 10000000	 0x80  	short string mask	*/ static final int SHORT_STRING_MASK     = 0x80;
	/* 0XXXXXXX	 0x80	start short string      */ static final int SHORT_STRING_VALUE    = 0x00;
	/* 01111111	 0x80	start short string      */ static final int SHORT_STRING_ARG_MASK = 0x7f;

	/* 11000000	 0xc0	start short array       */ static final int SHORT_ARRAY_MASK      = 0xc0;
	/* 10XXXXXX	 0x40	start short array       */ static final int SHORT_ARRAY_VALUE     = 0x80;
	/* 00111111	 0x80	start short string      */ static final int SHORT_ARRAY_ARG_MASK  = 0x3f;
                                                                                                  
	/* 11100000	 0xe0	short object mask       */ static final int SHORT_OBJECT_MASK     = 0xe0;
	/* 110XXXXX	 0xc0	start short object      */ static final int SHORT_OBJECT_VALUE    = 0xc0;
	/* 00011111	 0x80	start short string      */ static final int SHORT_OBJECT_ARG_MASK = 0x1F;
                                                                                                  
	/* 1111XXXX	 0x40	short number mask       */ static final int SHORT_NUMBER_MASK     = 0xf0;
	/* 1110XXXX	 0xc0	start short number      */ static final int SHORT_NUMBER_VALUE    = 0xe0;
	/* 00001111	 0x80	start short string      */ static final int SHORT_NUMBER_ARG_MASK = 0x0f;
	/*                                              */                                        
	/* 11110000	 0xf0	start object            */ static final int START_OBJECT          = 0xf0;
	/* 11110001	 0xf1	end object              */ static final int END_OBJECT            = 0xf1;
	/* 11110010	 0xf2	start array             */ static final int START_ARRAY           = 0xf2;
	/* 11110011	 0xf3	end array               */ static final int END_ARRAY             = 0xf3;
	/* 11110100	 0xf4	start string            */ static final int START_STRING          = 0xf4;
	/* 11110101	 0xf5	start number            */ static final int START_NUMBER          = 0xf5;
	/* 11110110	 0xf6	null                    */ static final int VALUE_NULL            = 0xf6;
	/* 11110111	 0xf7	boolean true            */ static final int VALUE_TRUE            = 0xf7;
	/* 11111000      0xf8	boolean false           */ static final int VALUE_FALSE           = 0xf8;

	/* 11111001      0xf9	double value            */ static final int DOUBLE_VALUE          = 0xf9;
	/* 11111010      0xfa	double value            */ static final int FLOAT_VALUE           = 0xfa;
	
	// Not so sure these are important:
	/* 11111011      0xfb	double value            */ static final int DOUBLE_TO_4           = 0xfb;
	/* 11111100      0xfc	double value            */ static final int DOUBLE_TO_3           = 0xfc;
	/* 11111101      0xfd	double value            */ static final int DOUBLE_TO_2           = 0xfd;
	/* 11111110      0xfe	RESERVED                */ static final int DOUBLE_TO_1           = 0xfe;
	
	/* 11111111      0xfa	RESERVED                */ static final int RESERVED              = 0xff;



	static boolean isShortString(int b)
	{
		return (SHORT_STRING_MASK & b) == SHORT_STRING_VALUE;
	}
	static int getShortStringArg(int b)
	{
		return SHORT_STRING_ARG_MASK & b;
	}
	
	static boolean isShortArray(int b)
	{
		return (SHORT_ARRAY_MASK & b) == SHORT_ARRAY_VALUE;
	}
	static int getShortArrayArg(int b)
	{
		return SHORT_ARRAY_ARG_MASK & b;
	}
	
	static boolean isShortObject(int b)
	{
		return (SHORT_OBJECT_MASK & b) == SHORT_OBJECT_VALUE;
	}
	static int getShortObjectArg(int b)
	{
		return SHORT_OBJECT_ARG_MASK & b;
	}
	
	static boolean isShortNumber(int b)
	{
		return (SHORT_NUMBER_MASK & b) == SHORT_NUMBER_VALUE;
	}
	static int getShortNumberArg(int b)
	{
		return SHORT_NUMBER_ARG_MASK & b;
	}
	

	static final int ESCAPE_BYTE = 0;
}
