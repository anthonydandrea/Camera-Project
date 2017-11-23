
#include <stdio.h>
#include <stdlib.h>

int main() {

  FILE *fileptr;
  char buffer;
  long filelen;

  fileptr = fopen("media/1.jpg", "rb");  // Open the file in binary mode
  fseek(fileptr, 0, SEEK_END);          // Jump to the end of the file
  filelen = ftell(fileptr);             // Get the current byte offset in the file
  rewind(fileptr);                      // Jump back to the beginning of the file

  char* buf = &buffer;
  buf = (char *)malloc((filelen+1)*sizeof(char)); // Enough memory for file + \0
  fread(buf, filelen, 1, fileptr); // Read in the entire file
  printf("%s\n", buf );
  fclose(fileptr); // Close the file

}
