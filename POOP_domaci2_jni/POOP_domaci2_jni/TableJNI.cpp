#include "application_Table.h"
#include "helper.h"
#include "TableJNI.h"
#include <sstream>
#include <iostream>
#include "CalculationError.h"
using namespace std;

/*
U properties C++ -> Precompiled Headers -> Not using precompiled headers
*/

//Konstruktor, stvara tabelu na osnovu zadatog CSV zapisa primljenog iz Jave
TableJNI::TableJNI(string csvTable) {
	istringstream iss(csvTable);
	string line;
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

		data.push_back(row);
	}
}

//Stampa tabelu u CSV formatu u konzoli
void TableJNI::printTableCSV() {
	for (auto row : data) {
		for (auto cell : row) {
			cout << cell << ",";
		}
		cout << endl;
	}
}

//Vraca sadrzaj celije sa indeksima (row, col), ako takva postoji; Ako ne postoji vraca "ERROR"
string TableJNI::getCellValue(int row, int col) {
	if (row >= getNumOfRows() || col >= getNumOfColumns()) return "ERROR";
	return data[row][col];
}

//Vraca sadrzaj celije zadatog imena, ako takva postoji; Ako ne postoji vraca "ERROR"
string TableJNI::getCellValue(string name) {
	pair<int, int> indices = cellNameToIndex(name);
	return getCellValue(indices.first, indices.second);
}

//Vraca par indeksa celije na osnovu njenog imena, pretpostavka je da je ime celije ispravno
pair<int, int> TableJNI::cellNameToIndex(string name) {
	char col = toupper(name[0]);
	int row = stoi(name.substr(1, name.length() - 1));
	return pair<int, int>(row - 1, col - 'A');
}

//Vraca string koji predstavlja ime celije, na osnovu zadatih indeksa, pretpostavka je da su indeksi ispravni
string TableJNI::indexToCellName(int row, int col) {
	stringstream ss;
	ss << (char)(col + 'A') << row + 1;
	return ss.str();
}

//Vraca string koji predstavlja tabelu u CSV formatu
string TableJNI::convertTableToCSV() {
	stringstream ss;
	for (int i = 0; i < getNumOfRows(); i++) {
		for (int j = 0; j < getNumOfColumns(); j++) {
			ss << data[i][j];
			ss << (j == getNumOfColumns() - 1 ? "\n" : ",");
		}
	}
	return ss.str();
}

//Vraca mapu koja preslikava par indeksa u string koji predstavlja izraz formule bez pocetnog znaka '='
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

//Deli zadati izraz (bez pocetnog znaka '=') na tokene i vraca vektor stringova koji su ti tokeni
vector<string> TableJNI::extractTokens(string expression) {
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

/*
Vraca mapu koja preslikava par indeksa u vektor tokena(stringova),
na osnovu zadate mape koja preslikava par indeksa u matematicki izraz(string)
*/
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

//Proverava da li zadati vektor tokena(stringova) sadrzi sve ispravne tokene; vraca true ako su svi tokeni ispravni
bool TableJNI::hasValidOps(vector<string> ops) {
	for (auto o : ops) {
		if (!isNumber(o) && !isFunction(o) && !isCellName(o) && !isOperator(o)) return false;
	}
	return true;
}

//Menja sve formule u tabeli stringovima koji predstavljaju njihove izracunate vrednosti, ili "ERROR" ako su izrazi neizracunljivi
void TableJNI::resolveFormulas() {
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
							token = "ERROR";
							//unableToSolve = true;
							changed = true;
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
}