package brs.http;

import static brs.http.JSONResponses.INCORRECT_DELTA_QUANTITY;
import static brs.http.JSONResponses.MISSING_DELTA_QUANTITY;
import static brs.http.JSONResponses.UNKNOWN_GOODS;
import static brs.http.common.Parameters.DELTA_QUALITY_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import brs.Account;
import brs.Blockchain;
import brs.BurstException;
import brs.DigitalGoodsStore.Goods;
import brs.TransactionProcessor;
import brs.common.QuickMocker;
import brs.common.QuickMocker.MockParam;
import brs.services.AccountService;
import brs.services.ParameterService;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class DGSQuantityChangeTest extends AbstractTransactionTest {

  private DGSQuantityChange t;

  private ParameterService mockParameterService;
  private Blockchain mockBlockchain;
  private TransactionProcessor mockTransactionProcessor;
  private AccountService mockAccountService;

  @Before
  public void setUp() {
    mockParameterService = mock(ParameterService.class);
    mockBlockchain = mock(Blockchain.class);
    mockTransactionProcessor = mock(TransactionProcessor.class);
    mockAccountService = mock(AccountService.class);

    t = new DGSQuantityChange(mockParameterService, mockTransactionProcessor, mockBlockchain, mockAccountService);
  }

  @Test
  public void processRequest() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest(
        new MockParam(DELTA_QUALITY_PARAMETER, 10)
    );

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(false);
    when(mockGoods.getSellerId()).thenReturn(1L);

    final Account mockSenderAccount = mock(Account.class);
    when(mockSenderAccount.getId()).thenReturn(1L);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    prepareTransactionTest(req, mockParameterService, mockTransactionProcessor);

    t.processRequest(req);
  }

  @Test
  public void processRequest_unknownGoodsBecauseDelisted() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest();

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(true);

    final Account mockSenderAccount = mock(Account.class);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    assertEquals(UNKNOWN_GOODS, t.processRequest(req));
  }

  @Test
  public void processRequest_unknownGoodsBecauseWrongSellerId() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest();

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(false);
    when(mockGoods.getSellerId()).thenReturn(1L);

    final Account mockSenderAccount = mock(Account.class);
    when(mockSenderAccount.getId()).thenReturn(2L);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    assertEquals(UNKNOWN_GOODS, t.processRequest(req));
  }

  @Test
  public void processRequest_missingDeltaQuantity() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest(
        new MockParam(DELTA_QUALITY_PARAMETER, null)
    );

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(false);
    when(mockGoods.getSellerId()).thenReturn(1L);

    final Account mockSenderAccount = mock(Account.class);
    when(mockSenderAccount.getId()).thenReturn(1L);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    assertEquals(MISSING_DELTA_QUANTITY, t.processRequest(req));
  }

  @Test
  public void processRequest_deltaQuantityWrongFormat() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest(
        new MockParam(DELTA_QUALITY_PARAMETER, "Bob")
    );

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(false);
    when(mockGoods.getSellerId()).thenReturn(1L);

    final Account mockSenderAccount = mock(Account.class);
    when(mockSenderAccount.getId()).thenReturn(1L);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    assertEquals(INCORRECT_DELTA_QUANTITY, t.processRequest(req));
  }

  @Test
  public void processRequest_deltaQuantityOverMaxIncorrectDeltaQuantity() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest(
        new MockParam(DELTA_QUALITY_PARAMETER, Integer.MIN_VALUE)
    );

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(false);
    when(mockGoods.getSellerId()).thenReturn(1L);

    final Account mockSenderAccount = mock(Account.class);
    when(mockSenderAccount.getId()).thenReturn(1L);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    assertEquals(INCORRECT_DELTA_QUANTITY, t.processRequest(req));
  }

  @Test
  public void processRequest_deltaQuantityLowerThanNegativeMaxIncorrectDeltaQuantity() throws BurstException {
    final HttpServletRequest req = QuickMocker.httpServletRequest(
        new MockParam(DELTA_QUALITY_PARAMETER, Integer.MAX_VALUE)
    );

    final Goods mockGoods = mock(Goods.class);
    when(mockGoods.isDelisted()).thenReturn(false);
    when(mockGoods.getSellerId()).thenReturn(1L);

    final Account mockSenderAccount = mock(Account.class);
    when(mockSenderAccount.getId()).thenReturn(1L);

    when(mockParameterService.getSenderAccount(eq(req))).thenReturn(mockSenderAccount);
    when(mockParameterService.getGoods(eq(req))).thenReturn(mockGoods);

    assertEquals(INCORRECT_DELTA_QUANTITY, t.processRequest(req));
  }

}
