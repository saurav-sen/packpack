package com.pack.pack.services.ext.text.summerize;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Saurav
 *
 */
public class STOP_WORDS {
	
	public static final String[] STOP_WORDS = new String[] { "a", "able",
			"about", "after", "all", "also", "am", "an", "and", "any", "are",
			"as", "at", "be", "because", "been", "but", "by", "can", "cannot",
			"could", "did", "do", "does", "either", "else", "ever", "every",
			"for", "from", "get", "got", "had", "has", "have", "he", "her",
			"hers", "him", "his", "how", "I", "if", "in", "into", "is", "it",
			"its", "just", "let", "like", "likely", "may", "me", "might",
			"most", "must", "my", "neither", "no", "nor", "not", "of", "off",
			"often", "on", "only", "or", "other", "our", "own", "said", "say",
			"says", "she", "should", "so", "some", "than", "that", "the",
			"their", "them", "then", "there", "these", "they", "this",
			"they're", "to", "too", "that's", "us", "was", "we", "were",
			"what", "when", "where", "which", "while", "who", "whom", "why",
			"will", "with", "would", "yet", "you", "your", "you're", "!!",
			"?!", "??", "!?", "`", "``", "''", "-lrb-", "-rrb-", "-lsb-",
			"-rsb-", "", ".", ":", ";", "\"", "'", "?", "<", ">", "{", "}",
			"[", "]", "+", "-", "(", ")", "&", "%", "$", "@", "!", "^", "#",
			"*", "..", "...", "'ll", "'s", "'m", "above", "again", "against",
			"aren't", "before", "being", "below", "between", "both", "can't",
			"couldn't", "didn't", "doesn't", "doing", "don't", "down",
			"during", "each", "few", "further", "hadn't", "hasn't", "haven't",
			"having", "he'd", "he'll", "he's", "here", "here's", "herself",
			"himself", "how's", "i", "i'd", "i'll", "i'm", "i've", "isn't",
			"it's", "itself", "let's", "more", "mustn't", "myself", "once",
			"ought", "ours ", "ourselves", "out", "over", "same", "shan't",
			"she'd", "she'll", "she's", "shouldn't", "such", "theirs",
			"themselves", "there's", "they'd", "they'll", "they've", "those",
			"through", "under", "until", "up", "very", "wasn't", "we'd",
			"we'll", "we're", "we've", "weren't", "what's", "when's",
			"where's", "who's", "why's", "won't", "wouldn't", "you'd",
			"you'll", "you've", "yours", "yourself", "yourselves", "###",
			"return", "arent", "cant", "couldnt", "didnt", "doesnt", "dont",
			"hadnt", "hasnt", "havent", "hes", "heres", "hows", "im", "isnt",
			"lets", "mustnt", "shant", "shes", "shouldnt", "thats", "theres",
			"theyll", "theyre", "theyve", "wasnt", "werent", "whats", "whens",
			"wheres", "whos", "whys", "wont", "wouldnt", "youd", "youll",
			"youre", "youve", "according", "accordingly", "across", "actually",
			"afterwards", "allow", "allows", "almost", "alone", "along",
			"already", "although", "always", "among", "amongst", "another",
			"anybody", "anyhow", "anyone", "anything", "anyway", "anyways",
			"anywhere", "apart", "appear", "appreciate", "appropriate",
			"around", "aside", "ask", "asking", "associated", "available",
			"away", "awfully", "b", "became", "become", "becomes", "becoming",
			"beforehand", "behind", "believe", "beside", "besides", "best",
			"better", "beyond", "brief", "c", "came", "cause", "causes",
			"certain", "certainly", "changes", "clearly", "co", "com", "come",
			"comes", "concerning", "consequently", "consider", "considering",
			"contain", "containing", "contains", "corresponding", "course",
			"currently", "d", "definitely", "described", "despite",
			"different", "done", "downwards", "e", "edu", "eg", "eight",
			"elsewhere", "enough", "entirely", "especially", "et", "etc",
			"even", "everybody", "everyone", "everything", "everywhere", "ex",
			"exactly", "example", "except", "f", "far", "fifth", "first",
			"five", "followed", "following", "follows", "former", "formerly",
			"forth", "four", "furthermore", "g", "gets", "getting", "given",
			"gives", "go", "goes", "going", "gone", "gotten", "greetings", "h",
			"happens", "hardly", "hello", "help", "hence", "hereafter",
			"hereby", "herein", "hereupon", "hi", "hither", "hopefully",
			"howbeit", "however", "ie", "ignored", "immediate", "inasmuch",
			"inc", "indeed", "indicate", "indicated", "indicates", "inner",
			"insofar", "instead", "inward", "j", "k", "keep", "keeps", "kept",
			"know", "known", "knows", "l", "last", "lately", "later", "latter",
			"latterly", "least", "less", "lest", "liked", "little", "ll",
			"look", "looking", "looks", "ltd", "m", "mainly", "many", "maybe",
			"mean", "meanwhile", "merely", "moreover", "mostly", "much", "n",
			"name", "namely", "nd", "near", "nearly", "necessary", "need",
			"needs", "never", "nevertheless", "new", "next", "nine", "nobody",
			"non", "none", "noone", "normally", "nothing", "novel", "now",
			"nowhere", "o", "obviously", "oh", "ok", "okay", "old", "one",
			"ones", "onto", "others", "otherwise", "ours", "outside",
			"overall", "p", "particular", "particularly", "per", "perhaps",
			"placed", "please", "plus", "possible", "presumably", "probably",
			"provides", "q", "que", "quite", "qv", "r", "rather", "rd", "re",
			"really", "reasonably", "regarding", "regardless", "regards",
			"relatively", "respectively", "right", "s", "saw", "saying",
			"second", "secondly", "see", "seeing", "seem", "seemed", "seeming",
			"seems", "seen", "self", "selves", "sensible", "sent", "serious",
			"seriously", "seven", "several", "shall", "since", "six",
			"somebody", "somehow", "someone", "something", "sometime",
			"sometimes", "somewhat", "somewhere", "soon", "sorry", "specified",
			"specify", "specifying", "still", "sub", "sup", "sure", "t",
			"take", "taken", "tell", "tends", "th", "thank", "thanks", "thanx",
			"thence", "thereafter", "thereby", "therefore", "therein",
			"thereupon", "think", "third", "thorough", "thoroughly", "though",
			"three", "throughout", "thru", "thus", "together", "took",
			"toward", "towards", "tried", "tries", "truly", "try", "trying",
			"twice", "two", "u", "un", "unfortunately", "unless", "unlikely",
			"unto", "upon", "use", "used", "useful", "uses", "using",
			"usually", "uucp", "v", "value", "various", "ve", "via", "viz",
			"vs", "w", "want", "wants", "way", "welcome", "well", "went",
			"whatever", "whence", "whenever", "whereafter", "whereas",
			"whereby", "wherein", "whereupon", "wherever", "whether",
			"whither", "whoever", "whole", "whose", "willing", "wish",
			"within", "without", "wonder", "x", "y", "yes", "z", "zero" };
	
	private static final Object OBJ = new Object();
	
	private static final Map<String, Object> STOP_WORDS_LOOKUP = new HashMap<String, Object>();
	
	static {
		for(int i=0; i<STOP_WORDS.length; i++) {
			STOP_WORDS_LOOKUP.put(STOP_WORDS[i], OBJ);
		}
	}
	
	private STOP_WORDS() {
	}
	
	public static final boolean isStopWord(String word) {
		if(word == null)
			return false;
		return STOP_WORDS_LOOKUP.get(word.toLowerCase()) != null;
	}

	/*public static void main(String[] args) {
		String[] stop_words = { "a","able","about","after","all","also","am",
		        "an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","did",
		        "do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","I",
		        "if","in","into","is","it","its","just","let","like","likely","may","me",
		        "might","most","must","my","neither","no","nor","not","of","off",
		        "often","on","only","or","other","our","own","said","say","says","she",
		        "should","so","some","than","that","the","their","them","then","there",
		        "these","they","this","they're","to","too","that's","us","was","we","were",
		        "what","when","where","which","while","who","whom","why","will","with",
		        "would","yet","you","your", "you're" };
		String[] stop_words1 = { "!!", "?!", "??", "!?", "`", "``", "''",
				"-lrb-", "-rrb-", "-lsb-", "-rsb-", "", "", ".", ":", ";",
				"\"", "'", "?", "<", ">", "{", "}", "[", "]", "+", "-", "(",
				")", "&", "%", "$", "@", "!", "^", "#", "*", "..", "...",
				"'ll", "'s", "'m", "a", "about", "above", "after", "again",
				"against", "all", "am", "an", "and", "any", "are", "aren't",
				"as", "at", "be", "because", "been", "before", "being",
				"below", "between", "both", "but", "by", "can", "can't",
				"cannot", "could", "couldn't", "did", "didn't", "do", "does",
				"doesn't", "doing", "don't", "down", "during", "each", "few",
				"for", "from", "further", "had", "hadn't", "has", "hasn't",
				"have", "haven't", "having", "he", "he'd", "he'll", "he's",
				"her", "here", "here's", "hers", "herself", "him", "himself",
				"his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if",
				"in", "into", "is", "isn't", "it", "it's", "its", "itself",
				"let's", "me", "more", "most", "mustn't", "my", "myself", "no",
				"nor", "not", "of", "off", "on", "once", "only", "or", "other",
				"ought", "our", "ours ", "ourselves", "out", "over", "own",
				"same", "shan't", "she", "she'd", "she'll", "she's", "should",
				"shouldn't", "so", "some", "such", "than", "that", "that's",
				"the", "their", "theirs", "them", "themselves", "then",
				"there", "there's", "these", "they", "they'd", "they'll",
				"they're", "they've", "this", "those", "through", "to", "too",
				"under", "until", "up", "very", "was", "wasn't", "we", "we'd",
				"we'll", "we're", "we've", "were", "weren't", "what", "what's",
				"when", "when's", "where", "where's", "which", "while", "who",
				"who's", "whom", "why", "why's", "with", "won't", "would",
				"wouldn't", "you", "you'd", "you'll", "you're", "you've",
				"your", "yours", "yourself", "yourselves", "###", "return",
				"arent", "cant", "couldnt", "didnt", "doesnt", "dont", "hadnt",
				"hasnt", "havent", "hes", "heres", "hows", "im", "isnt", "its",
				"lets", "mustnt", "shant", "shes", "shouldnt", "thats",
				"theres", "theyll", "theyre", "theyve", "wasnt", "were",
				"werent", "whats", "whens", "wheres", "whos", "whys", "wont",
				"wouldnt", "youd", "youll", "youre", "youve" };
		String[] stop_words2 = new String[] { "a", "able", "about", "above",
				"according", "accordingly", "across", "actually", "after",
				"afterwards", "again", "against", "all", "allow", "allows",
				"almost", "alone", "along", "already", "also", "although",
				"always", "am", "among", "amongst", "an", "and", "another",
				"any", "anybody", "anyhow", "anyone", "anything", "anyway",
				"anyways", "anywhere", "apart", "appear", "appreciate",
				"appropriate", "are", "around", "as", "aside", "ask", "asking",
				"associated", "at", "available", "away", "awfully", "b", "be",
				"became", "because", "become", "becomes", "becoming", "been",
				"before", "beforehand", "behind", "being", "believe", "below",
				"beside", "besides", "best", "better", "between", "beyond",
				"both", "brief", "but", "by", "c", "came", "can", "cannot",
				"cant", "cause", "causes", "certain", "certainly", "changes",
				"clearly", "co", "com", "come", "comes", "concerning",
				"consequently", "consider", "considering", "contain",
				"containing", "contains", "corresponding", "could", "course",
				"currently", "d", "definitely", "described", "despite", "did",
				"different", "do", "does", "doing", "done", "down",
				"downwards", "during", "e", "each", "edu", "eg", "eight",
				"either", "else", "elsewhere", "enough", "entirely",
				"especially", "et", "etc", "even", "ever", "every",
				"everybody", "everyone", "everything", "everywhere", "ex",
				"exactly", "example", "except", "f", "far", "few", "fifth",
				"first", "five", "followed", "following", "follows", "for",
				"former", "formerly", "forth", "four", "from", "further",
				"furthermore", "g", "get", "gets", "getting", "given", "gives",
				"go", "goes", "going", "gone", "got", "gotten", "greetings",
				"h", "had", "happens", "hardly", "has", "have", "having", "he",
				"hello", "help", "hence", "her", "here", "hereafter", "hereby",
				"herein", "hereupon", "hers", "herself", "hi", "him",
				"himself", "his", "hither", "hopefully", "how", "howbeit",
				"however", "i", "ie", "if", "ignored", "immediate", "in",
				"inasmuch", "inc", "indeed", "indicate", "indicated",
				"indicates", "inner", "insofar", "instead", "into", "inward",
				"is", "it", "its", "itself", "j", "just", "k", "keep", "keeps",
				"kept", "know", "known", "knows", "l", "last", "lately",
				"later", "latter", "latterly", "least", "less", "lest", "let",
				"like", "liked", "likely", "little", "ll", "look", "looking",
				"looks", "ltd", "m", "mainly", "many", "may", "maybe", "me",
				"mean", "meanwhile", "merely", "might", "more", "moreover",
				"most", "mostly", "much", "must", "my", "myself", "n", "name",
				"namely", "nd", "near", "nearly", "necessary", "need", "needs",
				"neither", "never", "nevertheless", "new", "next", "nine",
				"no", "nobody", "non", "none", "noone", "nor", "normally",
				"not", "nothing", "novel", "now", "nowhere", "o", "obviously",
				"of", "off", "often", "oh", "ok", "okay", "old", "on", "once",
				"one", "ones", "only", "onto", "or", "other", "others",
				"otherwise", "ought", "our", "ours", "ourselves", "out",
				"outside", "over", "overall", "own", "p", "particular",
				"particularly", "per", "perhaps", "placed", "please", "plus",
				"possible", "presumably", "probably", "provides", "q", "que",
				"quite", "qv", "r", "rather", "rd", "re", "really",
				"reasonably", "regarding", "regardless", "regards",
				"relatively", "respectively", "right", "s", "said", "same",
				"saw", "say", "saying", "says", "second", "secondly", "see",
				"seeing", "seem", "seemed", "seeming", "seems", "seen", "self",
				"selves", "sensible", "sent", "serious", "seriously", "seven",
				"several", "shall", "she", "should", "since", "six", "so",
				"some", "somebody", "somehow", "someone", "something",
				"sometime", "sometimes", "somewhat", "somewhere", "soon",
				"sorry", "specified", "specify", "specifying", "still", "sub",
				"such", "sup", "sure", "t", "take", "taken", "tell", "tends",
				"th", "than", "thank", "thanks", "thanx", "that", "thats",
				"the", "their", "theirs", "them", "themselves", "then",
				"thence", "there", "thereafter", "thereby", "therefore",
				"therein", "theres", "thereupon", "these", "they", "think",
				"third", "this", "thorough", "thoroughly", "those", "though",
				"three", "through", "throughout", "thru", "thus", "to",
				"together", "too", "took", "toward", "towards", "tried",
				"tries", "truly", "try", "trying", "twice", "two", "u", "un",
				"under", "unfortunately", "unless", "unlikely", "until",
				"unto", "up", "upon", "us", "use", "used", "useful", "uses",
				"using", "usually", "uucp", "v", "value", "various", "ve",
				"very", "via", "viz", "vs", "w", "want", "wants", "was", "way",
				"we", "welcome", "well", "went", "were", "what", "whatever",
				"when", "whence", "whenever", "where", "whereafter", "whereas",
				"whereby", "wherein", "whereupon", "wherever", "whether",
				"which", "while", "whither", "who", "whoever", "whole", "whom",
				"whose", "why", "will", "willing", "wish", "with", "within",
				"without", "wonder", "would", "would", "x", "y", "yes", "yet",
				"you", "your", "yours", "yourself", "yourselves", "z", "zero" };
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		final Object OBJ = new Object();
		for(String stop_word : stop_words) {
			map.put(stop_word, OBJ);
		}
		for(String stop_word : stop_words1) {
			map.put(stop_word, OBJ);
		}
		for(String stop_word : stop_words2) {
			map.put(stop_word, OBJ);
		}
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<String> itr = map.keySet().iterator();
		stringBuilder.append("{");
		while(itr.hasNext()) {
			String stop_word = itr.next();
			stringBuilder.append("\"");
			if(stop_word.equals("\"")) {
				stringBuilder.append("\\\"");
			} else {
				stringBuilder.append(stop_word);
			}
			stringBuilder.append("\", ");
		}
		stringBuilder.append("\"");
		stringBuilder.append("}");
		System.out.println(stringBuilder.toString());
	}*/
}
