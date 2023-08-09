#ifndef TABLE_JNI
#define TABLE_JNI
#include <vector>
#include <string>
#include <map>
using namespace std;
class TableJNI {
public:
	//Konstruktor, stvara tabelu na osnovu zadatog CSV zapisa primljenog iz Jave
	TableJNI(string csvTable);

	//Stampa tabelu u CSV formatu u konzoli
	void printTableCSV();

	//Vraca sadrzaj celije sa indeksima (row, col), ako takva postoji; Ako ne postoji vraca "ERROR"
	string getCellValue(int row, int col);

	//Vraca sadrzaj celije zadatog imena, ako takva postoji; Ako ne postoji vraca "ERROR"
	string getCellValue(string name);

	//Vraca par indeksa celije na osnovu njenog imena, pretpostavka je da je ime celije ispravno
	static pair<int, int> cellNameToIndex(string name);

	//Vraca string koji predstavlja ime celije, na osnovu zadatih indeksa, pretpostavka je da su indeksi ispravni
	static string indexToCellName(int row, int col);

	int getNumOfRows() {
		return data.size();
	}

	int getNumOfColumns() {
		return data[0].size();
	}

	//Vraca string koji predstavlja tabelu u CSV formatu
	string convertTableToCSV();

	//Vraca mapu koja preslikava par indeksa u string koji predstavlja izraz formule bez pocetnog znaka '='
	map<pair<int, int>, string> extractFormulas();

	//Deli zadati izraz (bez pocetnog znaka '=') na tokene i vraca vektor stringova koji su ti tokeni
	vector<string> extractTokens(string expression);

	/*
	Vraca mapu koja preslikava par indeksa u vektor tokena(stringova),
	na osnovu zadate mape koja preslikava par indeksa u matematicki izraz(string)
	*/
	map<pair<int, int>, vector<string>> extractExpressionTokens(map<pair<int, int>, string> formulaMap);

	//Proverava da li zadati vektor tokena(stringova) sadrzi sve ispravne tokene; vraca true ako su svi tokeni ispravni
	bool hasValidOps(vector<string> ops);

	//Menja sve formule u tabeli stringovima koji predstavljaju njihove izracunate vrednosti, ili "ERROR" ako su izrazi neizracunljivi
	void resolveFormulas();

private:
	vector<vector<string>> data;
};

#endif