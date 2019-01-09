/* Stupid hello world to show memory ops, and byte-level ops */

#include <stdio.h>
#include <string.h>

int main(void)
{
  char mystr[15] = {'H','e','l','l','o',',',' ','w','o','r','l','d','.','\n'};
  int i;

  for (i=0; i<strlen(mystr); i++)
    printf("%c",mystr[i]);

  return 0;
}


