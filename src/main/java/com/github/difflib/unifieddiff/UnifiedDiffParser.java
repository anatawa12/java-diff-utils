import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import java.util.ArrayList;
import java.util.List;
    static final Pattern UNIFIED_DIFF_CHUNK_REGEXP = Pattern.compile("^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@");

    private final UnifiedDiffLine[] MAIN_PARSER_RULES = new UnifiedDiffLine[]{
        new UnifiedDiffLine(true, "^diff\\s", this::processDiff),
        new UnifiedDiffLine(true, "^index\\s[\\da-zA-Z]+\\.\\.[\\da-zA-Z]+(\\s(\\d+))?$", this::processIndex),
        new UnifiedDiffLine(true, "^---\\s", this::processFromFile),
        new UnifiedDiffLine(true, "^\\+\\+\\+\\s", this::processToFile),
        new UnifiedDiffLine(true, UNIFIED_DIFF_CHUNK_REGEXP, this::processChunk)

    private UnifiedDiff parse() throws IOException, UnifiedDiffParserException {
            LOG.log(Level.INFO, "parsing line {0}", line);
    public static UnifiedDiff parseUnifiedDiff(InputStream stream) throws IOException, UnifiedDiffParserException {
    private boolean processLine(boolean header, String line) throws UnifiedDiffParserException {
        for (UnifiedDiffLine rule : MAIN_PARSER_RULES) {
            data.addFile(actualFile);
    public void processDiff(MatchResult match, String line) {
        actualFile.setDiffCommand(line);
    }

    public void processChunk(MatchResult _match, String chunkStart) {
        MatchResult match = _match;
        try {

            while (true) {

                List<String> originalTxt = new ArrayList<>();
                List<String> revisedTxt = new ArrayList<>();

                int old_ln = match.group(1) == null ? 1 : Integer.parseInt(match.group(1));
                int new_ln = match.group(3) == null ? 1 : Integer.parseInt(match.group(3));
                if (old_ln == 0) {
                    old_ln = 1;
                }
                if (new_ln == 0) {
                    new_ln = 1;
                }

                while (this.READER.ready()) {
                    String line = READER.readLine();
                    LOG.log(Level.INFO, "processing chunk line {0}", line);

                    if (line.startsWith(" ") || line.startsWith("+")) {
                        revisedTxt.add(line.substring(1));
                    }
                    if (line.startsWith(" ") || line.startsWith("-")) {
                        originalTxt.add(line.substring(1));
                    }
                    if (line.equals("") || line.startsWith("@@") || line.startsWith("--")) {
                        break;
                    }
                }

                actualFile.getPatch().addDelta(new ChangeDelta<>(new Chunk<>(
                        old_ln - 1, originalTxt), new Chunk<>(
                        new_ln - 1, revisedTxt)));

                if (READER.lastLine().equals("")
                        || READER.lastLine().startsWith("--")
                        || !READER.lastLine().startsWith("@@")) {
                    break;
                } else {
                    Matcher m = UNIFIED_DIFF_CHUNK_REGEXP.matcher(READER.lastLine());
                    if (m.find()) {
                        match = m.toMatchResult();
                    } else {
                        break;
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(UnifiedDiffParser.class.getName()).log(Level.SEVERE, null, ex);
            throw new UnifiedDiffParserException(ex);
        }
    public void processIndex(MatchResult match, String line) {
    private void processFromFile(MatchResult match, String line) {
        initFileIfNecessary();
        actualFile.setFromFile(extractFileName(line));
    }

    private void processToFile(MatchResult match, String line) {
        initFileIfNecessary();
        actualFile.setToFile(extractFileName(line));
    }

    private String extractFileName(String line) {
        return line.substring(4).replaceFirst("^(a|b)\\/", "");
    }

        public UnifiedDiffLine(boolean stopsHeaderParsing, Pattern pattern, BiConsumer<MatchResult, String> command) {
            this.pattern = pattern;
            this.command = command;
            this.stopsHeaderParsing = stopsHeaderParsing;
        }

        public boolean processLine(String line) throws UnifiedDiffParserException {