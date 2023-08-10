#ifndef HELPER_JNI
#define HELPER_JNI
#include <string>
#include <vector>

using namespace std;

//Uklanja sve blanko znakove iz stringa
string removeBlanks(string s);

//Proverava da li string predstavlja validno ime celije
bool isCellName(string name);

//Proveraca da li string predstavlja neku funkciju u formuli
bool isFunction(string name);

//Proverava da li string predstavlja broj(double)
bool isNumber(string name);

//Proverava da li string predstavlja neki operator ili zagradu
bool isOperator(string name);

//Proverava da li je string neki nevalidni token u formuli
bool isIllegalToken(string name);

string getNextTokenPostfix(string& postfix, int& index);

string getNextToken(string& input, int& index);

//Pretvara infiksni izraz u postfiksni
string infixToPostfix(string expression);

//Racunanje izraza u postfiksnom obliku
double calculatePostfix(string postfix);

//Pretvara funkciju SUM u vektor sabiraka
vector<string> sumFunction(string startCell, string endCell);

//Pretvara funkciju AVG u vektor operanada
vector<string> avgFunction(string startCell, string endCell);

//Menja token koji predstavlja funkciju vektorom tokena koji odgovaraju toj funkciji
vector<string> convertFormulaFunctionToTokens(string function);

#endif