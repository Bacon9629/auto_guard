package com.bacon.auto_guard.ui.robot;

class X_Y_Convert {
    private float x,y;
    public String convert(float x, float y){
        this.x = x;
        this.y = y;

        if (circle())
            return "000";
        else if (check_A())
            return "100";
        else if (check_B())
            return "110";
        else if (check_C())
            return "010";
        else if (check_D())
            return "011";
        else if (check_E())
            return "001";
        else if (check_F())
            return "111";
        else
            return "000";
    }

    private boolean circle(){
        //若是不符合，回傳true
        
        float h = 248,k = 230;
        float a = (x-h)*(x-h);
        float b = (y-k)*(y-k);
        float r2 = 120*120;
        
        return a+b < r2;
        
    }

    private boolean check_A(){
        //左迴轉
        return !check_a() && !check_c();
    }
    private boolean check_B(){
        //偏左轉
        return check_a() && !check_b();
    }

    private boolean check_C(){
        //直走
        return check_b() && !check_c();
    }

    private boolean check_D(){
        //偏右轉
        return check_c() && check_d();
    }

    private boolean check_E(){
        //右迴轉
        return !check_d() && check_b();
    }

    private boolean check_F(){
        //後退
        return check_c() && !check_b();
    }
    
    private boolean check_a(){
        return 69*x-248*y > -39928;
    }

    private boolean check_b(){
        return 115*x-49*y > 17250;
    }

    private boolean check_c(){
        return 115*x+49*y > 39790;
    }

    private boolean check_d(){
        return 23*x+74*y < 22724;
    }

}
