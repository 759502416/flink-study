package com.wakedata.whx.lambda;

/**
 * 匿名实现类
 *
 * @author :wanghuxiong
 * @title: AnonymltyImplement
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/13 11:41 下午
 */
public class AnonymltyImplement {

    interface Printer {
        /**
         * 输出
         *
         * @param val
         */
        void printer(String val);

    }

    public void printSomeThing(String something, Printer printer) {
        printer.printer(something);
    }

    /**
     * 主方法
     *
     * @param args
     */
    public static void main(String[] args) {
        AnonymltyImplement anonymltyImplement = new AnonymltyImplement();
        /*****不使用lambda表达式****/
        anonymltyImplement.printSomeThing("xiaocan", new Printer() {
            @Override
            public void printer(String val) {
                System.err.println(val);
            }
        });

        /******使用lambda表达式*****注意 lambda表达式只能实现一个方法，因此只能为只有一个抽象方法的接口创建对象****/
        //Printer printer = val -> System.err.println(val);
        anonymltyImplement.printSomeThing("xiaocan", (val) -> System.err.println(val));
    }
}
