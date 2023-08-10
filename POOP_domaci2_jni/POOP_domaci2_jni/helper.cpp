#include "helper.h"
#include <regex>
#include <sstream>
#include <map>
#include <set>
#include <stack>
#include "CalculationError.h"
#include <iostream>
#include <cctype>
#include <algorithm>
#include "TableJNI.h"

/*
U properties C++ -> Precompiled Headers -> Not using precompiled headers
*/

//Uklanja sve blanko znakove iz stringa
string removeBlanks(string s) {
	regex blanks("\\s+");
	return regex_replace(s, blanks, "");
}

//Proverava da li string predstavlja validno ime celije
bool isCellName(string name) {
	regex pattern("^[A-Za-z]\\d+$");
	return regex_match(name, pattern);
}

//Proveraca da li string predstavlja neku funkciju u formuli
bool isFunction(string name) {
	regex pattern("^[A-Za-z]+\\(.+\\)$");
	return regex_match(name, pattern);
}

//Proverava da li string predstavlja broj(double)
bool isNumber(string name) {
	if (name[0] == '-') {
		name = name.substr(1, name.length() - 1);
	}
	bool dot = false;
	for (char c : name) {
		if (c == '.' && dot) return false;
		if (c == '.') {
			dot = true;
			continue;
		}
		if (!isdigit(c)) return false;
	}
	return true;
}

//Proverava da li string predstavlja neki operator ili zagradu
bool isOperator(string name) {
	if (name == "+" || name == "-" || name == "*" || name == "/" || name == "(" || name == ")") return true;
	return false;
}

//Proverava da li je string neki nevalidni token u formuli
bool isIllegalToken(string name) {
	if (!isNumber(name) && !isFunction(name) && !isCellName(name) && !isOperator(name)) return true;
	return false;
}

//Pretvara infiksni izraz u postfiksni
string infixToPostfix(string expression) {
	//definisanje prioriteta operanada
	// @ je unarni -
	// # je unarni +

	map<string, int> inputPriority = { {"+",2},{"-",2},{"*",3},{"/",3},{"@",5},{"#",5}, {"(",10},{")",1} };
	map<string, int> stackPriority = { {"+",2},{"-",2},{"*",3},{"/",3},{"@",4},{"#",4}, {"(",0} };
	set<string> ops = { "+", "-", "*", "/", "(", ")", "@", "#" };
	int nextToRead = 0;
	stack<string> stack;
	int rank = 0;	//kontrola ispravnosti izraza
	string postfix;
	string next = getNextToken(expression, nextToRead);
	while (next != "") {
		if (ops.find(next) == ops.end()) {
			//token je operand, ide direktno u postfix izraz
			postfix += next + " ";
			rank++;
		}
		else {
			//token je operator
			while (!stack.empty() && (inputPriority[next] <= stackPriority[stack.top()])) {
				//vadimo operatore veceg prioriteta sa steka
				string op = stack.top();
				stack.pop();
				postfix += op + " ";
				rank = rank + ((op == "@" || op == "#") ? 0 : -1);
				if (rank < 1) throw CalculationError();
			}
			if (next != ")") {
				stack.push(next);
			}
			else {
				stack.pop();
			}
		}
		next = getNextToken(expression, nextToRead);
	}
	//procitan ceo infiks, popujemo ostatak sa steka
	while (!stack.empty()) {
		string op = stack.top();
		stack.pop();
		postfix += op + " ";
		rank = rank + ((op == "@" || op == "#") ? 0 : -1);
	}
	if (rank != 1) throw CalculationError();
	return postfix;
}

//Racunanje izraza u postfiksnom obliku
double calculatePostfix(string postfix) {
	stack<string> stack;
	int index = 0;
	string next = getNextTokenPostfix(postfix, index);
	set<string> ops = { "+", "-", "*", "/", "(", ")", "@", "#" };
	while (next != "") {
		if (ops.find(next) == ops.end()) {
			//operandi idu na stek
			stack.push(next);
		}
		else if (next == "@" || next == "#") {
			//unarni operatori skinu jedan operand sa steka i vrate novu vrednost
			double op = stod(stack.top());
			if (next == "@") op = -op;
			stringstream stream;
			stream << op;
			stack.pop();
			stack.push(stream.str());
		}
		else {
			//binarni operandi uzmu dve vrednosti sa steka i vrate jednu
			double op2 = stod(stack.top());
			stack.pop();
			double op1 = stod(stack.top());
			stack.pop();
			stringstream stream;
			double res = 0;
			if (next == "+") res = op1 + op2;
			else if (next == "-") res = op1 - op2;
			else if (next == "*") res = op1 * op2;
			else if (next == "/") {
				if (op2 == 0) {
					throw CalculationError();
				}
				res = op1 / op2;
			}
			stream << res;
			stack.push(stream.str());
		}
		next = getNextTokenPostfix(postfix, index);
	}
	double result = stod(stack.top());
	stack.pop();
	if (stack.empty()) return result;
	else throw CalculationError();
}

//Pretvara funkciju SUM u vektor sabiraka
vector<string> sumFunction(string startCell, string endCell) {
	vector<string> tokens;
	tokens.push_back("(");
	pair<int, int> startIndices = TableJNI::cellNameToIndex(startCell);
	pair<int, int> endIndices = TableJNI::cellNameToIndex(endCell);
	bool first = true;
	for (int i = startIndices.first; i <= endIndices.first; i++) {
		for (int j = startIndices.second; j <= endIndices.second; j++) {
			if (first) {
				first = false;
			}
			else {
				tokens.push_back("+");
			}
			string cellName = TableJNI::indexToCellName(i, j);
			tokens.push_back(cellName);
		}
	}
	tokens.push_back(")");
	return tokens;
}

//Pretvara funkciju AVG u vektor operanada
vector<string> avgFunction(string startCell, string endCell) {
	vector<string> tokens;
	tokens.push_back("(");
	pair<int, int> startIndices = TableJNI::cellNameToIndex(startCell);
	pair<int, int> endIndices = TableJNI::cellNameToIndex(endCell);
	int cnt = 0;
	bool first = true;
	tokens.push_back("(");
	for (int i = startIndices.first; i <= endIndices.first; i++) {
		for (int j = startIndices.second; j <= endIndices.second; j++) {
			if (first) {
				first = false;
			}
			else {
				tokens.push_back("+");
			}
			string cellName = TableJNI::indexToCellName(i, j);
			tokens.push_back(cellName);
			cnt++;
		}
	}
	tokens.push_back(")");
	tokens.push_back("/");
	tokens.push_back(to_string(cnt));
	tokens.push_back(")");
	return tokens;
}

//Menja token koji predstavlja funkciju vektorom tokena koji odgovaraju toj funkciji
vector<string> convertFormulaFunctionToTokens(string function) {
	vector<string> tokens;
	regex pattern("^([A-Za-z]+)\\(([A-Za-z]\\d+):([A-Za-z]\\d+)\\)$");
	smatch matches;
	if (!regex_match(function, matches, pattern)) {
		cout << "Greska u pretvaranju funckije" << endl;
		tokens.push_back(function);
		return tokens;
	}
	string functionName = matches[1];
	transform(functionName.begin(), functionName.end(), functionName.begin(), [](unsigned char c) {return toupper(c); });
	string startCell = matches[2];
	string endCell = matches[3];
	if (functionName == "SUM") {
		tokens = sumFunction(startCell, endCell);
	}
	else if (functionName == "AVG") {
		tokens = avgFunction(startCell, endCell);
	}
	else {
		//Nepostojeca funkcija
		tokens.push_back(function);
	}
	return tokens;
}

string getNextToken(string& input, int& index) {
	stringstream stream;
	set<char> ops = { '+','-','*','/','(',')' };
	while (index < input.length()) {
		char currentChar = input[index];
		if (ops.find(currentChar) != ops.end()) {
			//naisli smo na operator
			if (stream.str() != "") {
				//ako smo pre toga citali operand, prvo vracamo operand
				return stream.str();
			}
			//da li je operator unarni?
			if ((index > 0 && (currentChar == '+' || currentChar == '-') && \
				ops.find(input[index - 1]) != ops.end() && input[index - 1] != ')')\
				|| (index == 0 && (currentChar == '+' || currentChar == '-'))) {
				//jeste unarni
				stream << (currentChar == '+' ? "#" : "@");
				index++;
				return stream.str();
			}
			//nije unarni operator
			stream << currentChar;
			index++;
			return stream.str();
		}
		//nailazimo na operand
		stream << currentChar;
		index++;
	}
	return stream.str();
}

string getNextTokenPostfix(string& postfix, int& index) {
	stringstream stream;
	while (index < postfix.length()) {
		char currentChar = postfix[index++];
		if (currentChar == ' ') break;
		stream << currentChar;
	}
	return stream.str();
}


