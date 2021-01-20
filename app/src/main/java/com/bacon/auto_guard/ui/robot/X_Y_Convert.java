package com.bacon.auto_guard.ui.robot;

class X_Y_Convert {
    private int x,y;
    public int convert(int x, int y){
        this.x = x;
        this.y = y;

        if (circle())
            return 0;
        else if (check_A())
            return 2;
        else if (check_B())
            return 5;
        else if (check_C())
            return 1;
        else if (check_D())
            return 6;
        else if (check_E())
            return 3;
        else if (check_F())
            return 4;
        else
            return 0;
    }

    public int get_speed(int x, int y){
        this.x = x;
        this.y = y;

        if (circle())
            return 0;

        int h = 248,k = 230;
        int a = (x-h)*(x-h);
        int b = (y-k)*(y-k);
        int r2 = 70*70;
        return (int)Math.sqrt((a+b)-r2);

    }

    private boolean circle(){
        //若是不符合，回傳true
        int h = 248,k = 230;
        int a = (x-h)*(x-h);
        int b = (y-k)*(y-k);
        int r2 = 70*70;
        
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
