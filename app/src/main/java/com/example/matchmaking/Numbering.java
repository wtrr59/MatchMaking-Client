package com.example.matchmaking;

public class Numbering {
    public static int tier(String tier){
        if(tier.equals("Challenger")) return 0;
        else if(tier.equals("GrandMaster")) return 1;
        else if(tier.equals("Master")) return 2;
        else if(tier.equals("Diamond")) return 3;
        else if(tier.equals("Platinum")) return 4;
        else if(tier.equals("Gold")) return 5;
        else if(tier.equals("Silver")) return 6;
        else if(tier.equals("Bronze")) return 7;
        else if(tier.equals("Iron")) return 8;

        return 0;
    }

    public static int position(String position){
        if(position.equals("top"))return 0;
        else if(position.equals("jungle")) return 1;
        else if(position.equals("mid")) return 2;
        else if(position.equals("bottom")) return 3;
        else if(position.equals("support")) return 4;

        return 0;
    }

    public static int voice(String voice){
        if(voice.equals("가능")) return 0;
        else if(voice.equals("불가능")) return 1;
        else if(voice.equals("상관없음")) return 2;

        return 0;
    }

    public static int tendency(String tendency){
        if(tendency.equals("즐겜")) return 0;
        else if(tendency.equals("빡겜")) return 1;
        else if(tendency.equals("상관없음")) return 2;

        return 0;
    }

    public static int num(int num){
        if(num == 2) return 0;
        else if(num == 3) return 1;
        else if(num == 5) return 2;

        return 0;
    }

    public static int room(int tier, int tendency, int voice, int num){
        return tier*16 + tendency*8 + voice*4 + num;
    }
}
