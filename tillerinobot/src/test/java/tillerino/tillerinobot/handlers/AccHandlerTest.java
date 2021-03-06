package tillerino.tillerinobot.handlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.regex.Matcher;

import org.junit.Test;
import org.mockito.internal.matchers.Contains;
import org.tillerino.osuApiModel.OsuApiBeatmap;

import tillerino.tillerinobot.BeatmapMeta;
import tillerino.tillerinobot.UserException;
import tillerino.tillerinobot.BotBackend;
import tillerino.tillerinobot.IRCBot.IRCBotUser;
import tillerino.tillerinobot.UserDataManager.UserData;
import tillerino.tillerinobot.UserDataManager.UserData.BeatmapWithMods;
import tillerino.tillerinobot.diff.PercentageEstimates;
import tillerino.tillerinobot.lang.Default;
import tillerino.tillerinobot.lang.Language;


public class AccHandlerTest {
	@Test
	public void testExtendedPattern() throws Exception {
		assertTrue(AccHandler.extended.matcher("97.2 800x 1m").matches());
	}
	@Test
	public void testExtendedPattern2() throws Exception {
		Matcher matcher = AccHandler.extended.matcher("97.2% 800x 11m");
		assertTrue(matcher.matches());
		assertEquals("11", matcher.group(3));
	}
	
	@Test
	public void testSimple() throws Exception {
		BotBackend backend = mock(BotBackend.class);
		when(backend.loadBeatmap(anyInt(), anyLong(), any(Language.class)))
				.thenReturn(new BeatmapMeta(new OsuApiBeatmap(), null, mock(PercentageEstimates.class)));
		AccHandler accHandler = new AccHandler(backend);
		
		IRCBotUser user = mock(IRCBotUser.class);
		UserData userData = mock(UserData.class);
		when(userData.getLastSongInfo()).thenReturn(new BeatmapWithMods(0, 0));
		accHandler.handle("acc 97.5 800x 1m", user, null, userData);
		
		verify(user).message(contains("800x"));
	}
	
	@Test(expected=UserException.class)
	public void testLargeNumber() throws Exception {
		UserData userData = mock(UserData.class);
		when(userData.getLanguage()).thenReturn(new Default());
		when(userData.getLastSongInfo()).thenReturn(new BeatmapWithMods(0, 0));
		try {
			new AccHandler(null).handle("acc 99 80000000000000000000x 1m", null, null, userData);
			fail();
		} catch (Exception e) {
			assertThat(e.getMessage(), new Contains("800000000000"));
			throw e;
		}
	}
}
