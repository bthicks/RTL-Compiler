#include "driver.h"

int regtest2 (int a)
{
  int v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15;
  v1=getint();
  v2=getint();
  if (a > 0) {
    v3=a+v1;
    v4=a+v2;
  }
  else {
    v3=v1-a;
    v4=v2-a;
  }
  v5=v3+v4;
  v6=getint();
  v7=getint();
  if (v6 > v7) {
    v8=v6+1;
    v9=v7-1;
  }
  else {
    v9=v6+1;
    v8=v7-1;
  }
  v10=v8+v9;
  v11=getint();
  v12=getint();
  while (v11 > v12) {
    v11=getint();
    v12=getint();
  }
  v13=v11-v12;
  v14=v11+v12;
  v15=a+v14;

  if (v15 > 0)  
    return (v1+v2+v3+v4+v5+v6+v7+v8+v9+v10+v11+v12+v13+v14+v15);
  else
    return (v1-v2-v3-v4-v5-v6-v7-v8-v9-v10-v11-v12-v13-v14-v15);
}
