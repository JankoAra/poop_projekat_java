#include "application_Table.h"
#include "helper.h"
#include "TableJNI.h"
#include <sstream>
#include <regex>
#include <cctype>
#include <stack>
#include <set>
#include "CalculationError.h"
using namespace std;



void print(std::string line) {
	std::cout << line << std::endl;
}
string removeBlanks(string s) {
	regex blanks("\\s+");
	return regex_replace(s, blanks, "");
}

JNIEXPORT jstring JNICALL Java_application_Table_resolveTableFormulas
(JNIEnv* env, jobject, jstring csvTable) {
	std::string cstring = env->GetStringUTFChars(csvTable, nullptr);
	//cout << "\nCSV primljen u c++:\n";
	//cout << cstring;

	TableJNI* table = new TableJNI(cstring,1);
	//cout << "Tabela prima:\n";
	//table->printTableCSV();
	
	
	table->resolveFormulas();
	string res = table->convertTableToCSV();
	//cout << "Vracam:\n" << res;
	jstring ret = env->NewStringUTF(res.c_str());
	//cout << res << endl;
	delete table;
	//cout <<"|" << res << "|";
	return ret;
}

TableJNI::TableJNI(string csvTable, int k) {
	istringstream iss(csvTable);
	string line;
	int ri = 0;
	//cout << "kontruktor tabele" << endl;
	while (getline(iss, line)) {
		vector<string> row;
		string cell = "";
		for (char c : line) {
			if (c == ',') {
				row.push_back(cell);
				cell = "";
			}
			else {
				cell.push_back(c);
			}
		}
		row.push_back(cell);
		ri++;
		
		data.push_back(row);
	}
}

TableJNI::TableJNI(string csvTable) {
	istringstream iss(csvTable);
	string line;
	int ri = 0;
	cout << "kontruktor tabele" << endl;
	while (getline(iss, line)) {
		vector<string> row;
		//line += "\n";
		cout << "Linija " << (ri) << ": |" << line << "|";
		if (line[line.length() - 1] != '\n') {
			//cout << "ne zavrsava se sa \\n" << endl;
		}
		ri++;
		stringstream ss(line);
		string cell;
		int ci = 0;
		while (getline(ss, cell, ',')) {
			//cout << "celija" << (ci) << ":" << cell << endl;
			ci++;
			row.push_back(cell);
		}

		data.push_back(row);
	}
	//cout << getNumOfColumns() << endl;
	//cout << getNumOfRows() << endl;
}

void TableJNI::printTableCSV() {
	for (auto row : data) {
		for (auto cell : row) {
			cout << cell << ",";
		}
		cout << endl;
	}
}

string TableJNI::getCellValue(int row, int col) {
	if (row >= getNumOfRows() || col >= getNumOfColumns())return "ERROR";
	return data[row][col];
}

string TableJNI::getCellValue(string name) {
	pair<int, int> indices = cellNameToIndex(name);
	return getCellValue(indices.first, indices.second);
}

pair<int, int> TableJNI::cellNameToIndex(string name) {
	char col = toupper(name[0]);
	int row = stoi(name.substr(1, name.length() - 1));
	return pair<int, int>(row - 1, col - 'A');
}

string TableJNI::indexToCellName(int row, int col) {
	stringstream ss;
	ss << (char)(col + 'A') << row + 1;
	return ss.str();
}

bool isCellName(string name) {
	regex pattern("^[A-Za-z]\\d+$");
	return regex_match(name, pattern);
}

bool isFunction(string name) {
	regex pattern("^[A-Z]+\\(.+\\)$");
	return regex_match(name, pattern);
}

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

bool isOperator(string name) {
	if (name == "+" || name == "-" || name == "*" || name == "/" || name == "(" || name == ")") return true;
	return false;
}

bool isIllegalToken(string name) {
	if (!isNumber(name) && !isFunction(name) && !isCellName(name) && !isOperator(name)) return true;
	return false;
}

map<pair<int, int>, string> TableJNI::extractFormulas() {
	map<pair<int, int>, string> mapa;
	for (int i = 0; i < getNumOfRows(); i++) {
		for (int j = 0; j < getNumOfColumns(); j++) {
			string temp = data[i][j];
			string cell = removeBlanks(temp);
			if (cell[0] == '=') {
				mapa[{i, j}] = cell.substr(1, cell.length() - 1);
			}
		}
	}
	return mapa;
}

vector<string> TableJNI::extractTokens(string expression) {
	//expression je formula bez =
	vector<string> tokens;
	string currentToken = "";
	bool functionStarted = false;
	bool prevIsChar = false;
	for (int i = 0; i < expression.length(); i++) {
		char c = expression[i];
		if (functionStarted) {
			currentToken.push_back(c);
			if (c == ')') {
				functionStarted = false;
				prevIsChar = false;
			}
		}
		else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {
			if (!currentToken.empty()) {
				tokens.push_back(currentToken);
				currentToken.clear();
			}
			tokens.push_back(string(1, c));
			prevIsChar = false;
		}
		else if (isalpha(c)) {
			if (prevIsChar) {
				functionStarted = true;
			}
			currentToken.push_back(c);
			prevIsChar = true;
		}
		else {
			currentToken.push_back(c);
			prevIsChar = false;
		}
	}
	if (!currentToken.empty()) {
		tokens.push_back(currentToken);
	}
	return tokens;
}

map<pair<int, int>, vector<string>> TableJNI::extractExpressionTokens(map<pair<int, int>, string> formulaMap) {
	map<pair<int, int>, vector<string>> tokens;

	for (auto keyVal : formulaMap) {
		string expr = keyVal.second;
		vector<string> token = extractTokens(expr);
		tokens[keyVal.first] = token;
		/*cout << indexToCellName(keyVal.first.first, keyVal.first.second) << ":";
		for (auto s : token) {
			cout << s << ",";
		}
		bool valid = hasValidOps(token);
		cout << "------>" << (valid ? "ispravan" : "neispravan");
		cout << endl;*/
	}
	return tokens;
}

bool TableJNI::hasValidOps(vector<string> ops) {
	for (auto o : ops) {
		if (!isNumber(o) && !isFunction(o) && !isCellName(o) && !isOperator(o)) return false;
	}
	return true;
}

void TableJNI::resolveFormulas() {
	//cout << "resolve start" << endl;
	auto formulas = extractFormulas();
	auto unresolvedTokens = extractExpressionTokens(formulas);
	while (!unresolvedTokens.empty()) {
		//spoljna petlja, proverava da li je ostalo nekih nerazresenih celija
		bool changed = false;
		for (auto it = unresolvedTokens.begin(); it != unresolvedTokens.end();) {
			//unutrasnja petlja 1, proverava da li je jedna celija razresena
			auto keyVal = *it;
			pair<int, int> indices = keyVal.first;
			bool unableToSolve = false;
			for (auto& token : keyVal.second) {
				//unutrasnja petlja 2, razresava pojedinacne tokene u celiji
				if (isCellName(token)) {
					//pokusaj da razresis celiju
					string cellValue = getCellValue(token);
					auto tempInd = cellNameToIndex(token);
					if (tempInd == indices) {
						token = "ERROR";
						changed = true;
						continue;
					}
					if (cellValue == "") {
						token = "0";
						changed = true;
					}
					else if (cellValue[0] == '=') {
						unableToSolve = true;
					}
					else {
						if (cellValue == "ERROR") {
							token = cellValue;
							changed = true;
						}
						else if (!isNumber(cellValue)) {
							unableToSolve = true;
						}
						else {
							token = cellValue;
							changed = true;
						}
					}
				}
				else if (isFunction(token)) {
					//pokusaj da razresis funkciju
					token = "0";
					changed = true;
				}
				else if (isIllegalToken(token)) {
					token = "ERROR";
					changed = true;
				}
			}
			if (!unableToSolve) {
				//svi tokeni su razreseni, izracunaj izraz
				string expression = "";
				double val;
				string sval = "";
				for (auto token : keyVal.second) {
					expression += token;
					if (token == "ERROR") {
						sval = "ERROR";
						break;
					}
				}
				if (sval != "ERROR") {
					try {
						string postfix = infixToPostfix(expression);
						val = calculatePostfix(postfix);
						stringstream ss;
						ss << val;
						sval = ss.str();
					}
					catch (CalculationError err) {
						sval = "ERROR";
					}
				}
				
				data[indices.first][indices.second] = sval;


				//ukloni element iz liste nerazresenih
				it = unresolvedTokens.erase(it);
				changed = true;
			}
			else {
				it++;
			}
		}
		if (!changed) {
			//negde je zapelo, neka rekurzivna definicija
			cout << "Rekurzija ili greska" << endl;
			for (auto cells : unresolvedTokens) {
				auto ind = cells.first;
				data[ind.first][ind.second] = "ERROR";
			}
			return;
		}
	}
	//cout << "resolve end" << endl;
}

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

string TableJNI::convertTableToCSV() {
	stringstream ss;
	//cout << getNumOfColumns();
	for (int i = 0; i < getNumOfRows(); i++) {
		for (int j = 0; j < getNumOfColumns(); j++) {
			ss << data[i][j];
			ss << (j == getNumOfColumns() - 1 ? "\n" : ",");
		}
	}
	return ss.str();
}

