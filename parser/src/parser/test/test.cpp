

void fun1/**/(int a, /**/int b )//comment0
/*comment1*/
//comment2



{
    cout << "Hello World!";
    /* **/
    //}
    return ;
}

void fun2/*



*/
(int a, //comment
		int b )//comment0
/*comment1*/
//comment2



{
    cout << "Hello World!";
    /* **/
    //}
    return ;
}


void (fun3)//comment -1
/**/
//*/
//Comment
(int a, int b )//comment0
/*comment1*/
//comment2



{
    cout << "Hello World!";
    /* **/
    //}
    return ;
}

struct Sum
{
	int sum;
	Sum() : sum(0) { }
	void (operator())(int n) { sum += n; }

	void operator *(int n) { sum += n; }

};
