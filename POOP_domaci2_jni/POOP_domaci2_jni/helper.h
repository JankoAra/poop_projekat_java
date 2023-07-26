#ifndef HELPER_JNI
#define HELPER_JNI
#include <string>
#include <iostream>
#include <string>


using namespace std;

void print(std::string);

string removeBlanks(string s);
bool isCellName(string name);
bool isFunction(string name);
bool isNumber(string name);
bool isOperator(string name);
bool isIllegalToken(string name);
string getNextTokenPostfix(string& postfix, int& index);
string getNextToken(string& input, int& index);
string infixToPostfix(string expression);
double calculatePostfix(string postfix);

#endif