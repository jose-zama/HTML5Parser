package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Before_DOCTYPE_public_identifier_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		tokenizerContext.getCurrentInputCharacter();
		DocTypeToken docToken = null;

		switch (tokenizerContext.getCurrentASCIICharacter()) {

		// U+0009 CHARACTER TABULATION (tab)
		case TAB:

			// U+000A LINE FEED (LF)
		case LF:

			// U+000C FORM FEED (FF)
		case FF:

			// U+0020 SPACE
			// Ignore the character.
		case SPACE:
			// Ignore the character.
			break;

		// U+0022 QUOTATION MARK (")
		// Set the DOCTYPE token's public identifier to the empty string (not
		// missing), then switch to the DOCTYPE public identifier
		// (double-quoted) state.
		case QUOTATION_MARK:
			((DocTypeToken) tokenizerContext.getCurrentToken())
					.setPublicIdentifier("");
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.DOCTYPE_public_identifier_double_quoted_state));
			break;

		// "'" (U+0027)
		// Set the DOCTYPE token's public identifier to the empty string (not
		// missing), then switch to the DOCTYPE public identifier
		// (single-quoted) state.
		case APOSTROPHE:
			((DocTypeToken) tokenizerContext.getCurrentToken())
					.setPublicIdentifier("");
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.DOCTYPE_public_identifier_single_quoted_state));
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Parse error. Set the DOCTYPE token's force-quirks flag to on. Switch
		// to the data state. Emit that DOCTYPE token.
		case GREATER_THAN_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// EOF
		// Parse error. Switch to the data state. Set the DOCTYPE token's
		// force-quirks flag to on. Emit that DOCTYPE token. Reconsume the EOF
		// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setFlagEmitToken(true);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Parse error. Set the DOCTYPE token's force-quirks flag to on. Switch
		// to the bogus DOCTYPE state.
		default:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Bogus_DOCTYPE_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}