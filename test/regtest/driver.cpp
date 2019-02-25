
/* Driver for register-allocation with spilling example */
#include "driver.h"

// Prints an integer 
void printint (int n) {
  printf("printint: %d\n",n);
}

// Returns a random integer
int getint(void)
{
  int n = ((1000*(rand()-(RAND_MAX/2))/(RAND_MAX/1000)));
  printint(n);
  return n;
}	

////////////////////////////////////////////////////////////////////////////
// Program main
////////////////////////////////////////////////////////////////////////////
int main() {

  fprintf(stdout,"Seeding rand function:\n");
  // Seed the rand function for repeatable tests
  srand(2019);

  fprintf(stdout,"Calling first test function:\n");
  printint (regtest1( getint() ));

  fprintf(stdout,"Calling second test function:\n");
  printint (regtest2( getint() ));

  return 0;
}
