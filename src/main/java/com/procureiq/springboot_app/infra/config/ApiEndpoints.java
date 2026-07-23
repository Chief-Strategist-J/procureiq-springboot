package com.procureiq.springboot_app.infra.config;

public final class ApiEndpoints {
    public static final String API_V1 = "/api/v1";
    public static final String AUTH = API_V1 + "/auth";
    public static final String GITHUB = API_V1 + "/github";
    public static final String GMAIL = API_V1 + "/gmail";
    public static final String NOTIFICATIONS = API_V1 + "/notifications";
    public static final String CAMPAIGNS = API_V1 + "/campaigns";
    public static final String JOBS = API_V1 + "/jobs";
    public static final String WORKFLOWS = API_V1 + "/workflows";
    public static final String VOICE = API_V1 + "/voice";
    public static final String REMINDERS = API_V1 + "/reminders";
    public static final String CRYPTO = API_V1 + "/crypto";
    public static final String GITHUB_TEMPLATES = API_V1 + "/github/templates";
    
    public static final String FIELD_SERVICE = API_V1 + "/fieldservice";
    public static final String WORK_TYPES = FIELD_SERVICE + "/work-types";
    public static final String SERVICE_APPOINTMENTS = FIELD_SERVICE;
    public static final String SERVICE_RESOURCE_CAPACITIES = FIELD_SERVICE + "/service-resource-capacities";
    public static final String SERVICE_CREW_MEMBERS = FIELD_SERVICE + "/service-crew-members";
    public static final String RESOURCES = FIELD_SERVICE + "/resources";
    public static final String SERVICE_TERRITORY_MEMBERS = FIELD_SERVICE + "/service-territory-members";
    public static final String SKILLS = FIELD_SERVICE + "/skills";
    public static final String MILESTONES = FIELD_SERVICE + "/milestones";
    public static final String MAINTENANCE_PLANS = FIELD_SERVICE + "/maintenance-plans";
    public static final String CASE_MILESTONES = FIELD_SERVICE + "/case-milestones";
    public static final String TIME_SLOTS = FIELD_SERVICE + "/time-slots";
    public static final String RESOURCE_PREFERENCES = FIELD_SERVICE + "/resource-preferences";
    public static final String OPERATING_HOURS = FIELD_SERVICE + "/operating-hours";
    public static final String WORK_ORDERS = FIELD_SERVICE + "/work-orders";
    public static final String TERRITORIES = FIELD_SERVICE + "/territories";
    public static final String RESOURCE_ABSENCES = FIELD_SERVICE + "/resource-absences";
    public static final String SKILL_REQUIREMENTS = FIELD_SERVICE + "/skill-requirements";
    public static final String SERVICE_RESOURCE_SKILLS = FIELD_SERVICE + "/service-resource-skills";
    public static final String ASSET_RELATIONSHIPS = FIELD_SERVICE + "/asset-relationships";
    public static final String SERVICE_CREWS = FIELD_SERVICE + "/service-crews";
    public static final String SHIFTS = FIELD_SERVICE + "/shifts";

    
    public static final String PATH_ID = "/{id}";
    public static final String UNREAD_COUNT = "/unread-count";
    public static final String DEVICES = "/devices";
    public static final String STATUS_ID = "/{id}/status";
    
    public static final String SCHEDULES = "/schedules";
    public static final String SCHEDULES_ID = "/schedules/{id}";
    
    public static final String RECIPIENTS = "/recipients";
    public static final String RECIPIENTS_ID = "/recipients/{id}";
    
    public static final String DISPATCH = "/dispatch";
    public static final String REPO_INFO = "/repo-info";
    public static final String WORKFLOW_RUNS = "/workflow-runs";
    public static final String CREATE_WORKFLOW = "/create-workflow";
    public static final String DELETE_WORKFLOW = "/delete-workflow";
    
    public static final String APPOINTMENTS_SUB = "/appointments";
    public static final String APPOINTMENTS_ID = "/appointments/{id}";
    public static final String APPOINTMENTS_ASSIGN = "/appointments/{appointmentId}/assign";
    public static final String ASSIGNED_RESOURCES_ID = "/assigned-resources/{id}";
    public static final String APPOINTMENTS_CANDIDATES = "/appointments/{appointmentId}/candidates";
    
    public static final String TEMPLATES = "/templates";
    public static final String TEMPLATES_ID = "/templates/{id}";
    
    public static final String SCHEDULE = "/schedule";
    public static final String SCHEDULED = "/scheduled";
    
    public static final String SEND = "/send";
    public static final String LIST = "/list";
    public static final String RUNS = "/runs";
    public static final String SIGNUP = "/signup";
    public static final String LOGIN = "/login";
    public static final String FORGOT_PASSWORD = "/forgot-password";
    public static final String RESET_PASSWORD = "/reset-password";
    public static final String JOBS_ID_RUNS = "/{jobId}/runs";
    public static final String WORKFLOWS_ID_RUNS = "/{workflowId}/runs";
    
    public static final String KLINES       = "/klines";
    public static final String TICKER       = "/ticker";
    public static final String ORDERBOOK    = "/orderbook";
    public static final String STOCKS       = "/stocks";
    public static final String SYMBOLS      = "/symbols";
    public static final String DETAILS      = "/details";
    public static final String ANALYZE      = "/analyze";
    public static final String PRICE        = "/price";
    public static final String BOOK_TICKER  = "/book-ticker";
    public static final String TRADES       = "/trades";
    public static final String AVG_PRICE    = "/avg-price";
    public static final String ALL_TICKERS  = "/all-tickers";
    public static final String MOVERS       = "/movers";

    public static final String PYTHON_ANALYTICS_TIME_SERIES = API_V1 + "/analytics/time-series";
    
    public static final String BINANCE_BASE_URL              = "https://api.binance.com";
    public static final String BINANCE_KLINES_URI            = BINANCE_BASE_URL + "/api/v3/klines?symbol={symbol}&interval={interval}&limit={limit}";
    public static final String BINANCE_TICKER_24HR_URI       = BINANCE_BASE_URL + "/api/v3/ticker/24hr?symbol={symbol}";
    public static final String BINANCE_TICKER_ALL_URI        = BINANCE_BASE_URL + "/api/v3/ticker/24hr";
    public static final String BINANCE_DEPTH_URI             = BINANCE_BASE_URL + "/api/v3/depth?symbol={symbol}&limit={limit}";
    public static final String BINANCE_EXCHANGE_INFO_URI     = BINANCE_BASE_URL + "/api/v3/exchangeInfo";
    public static final String BINANCE_PRICE_URI             = BINANCE_BASE_URL + "/api/v3/ticker/price?symbol={symbol}";
    public static final String BINANCE_BOOK_TICKER_URI       = BINANCE_BASE_URL + "/api/v3/ticker/bookTicker?symbol={symbol}";
    public static final String BINANCE_TRADES_URI            = BINANCE_BASE_URL + "/api/v3/trades?symbol={symbol}&limit={limit}";
    public static final String BINANCE_AVG_PRICE_URI         = BINANCE_BASE_URL + "/api/v3/avgPrice?symbol={symbol}";
    public static final String BINANCE_HEADER_API_KEY        = "X-MBX-APIKEY";

    public static final String KEY_PRICE            = "price";
    public static final String KEY_BID_PRICE        = "bidPrice";
    public static final String KEY_BID_QTY          = "bidQty";
    public static final String KEY_ASK_PRICE        = "askPrice";
    public static final String KEY_ASK_QTY          = "askQty";
    public static final String KEY_TRADE_ID         = "id";
    public static final String KEY_QTY              = "qty";
    public static final String KEY_TRADE_TIME       = "time";
    public static final String KEY_IS_BUYER_MAKER   = "isBuyerMaker";
    public static final String KEY_MINS             = "mins";

    public static final String ALPHAVANTAGE_BASE_URL                  = "https://www.alphavantage.co/query";
    public static final String ALPHAVANTAGE_DAILY_TIME_SERIES_URI     = ALPHAVANTAGE_BASE_URL + "?function=TIME_SERIES_DAILY&symbol={symbol}&apikey={apiKey}";

    public static final String VAPI_ENV_API_KEY         = "VAPI_API_KEY";
    public static final String VAPI_ENV_PHONE_NUMBER_ID = "VAPI_PHONE_NUMBER_ID";
    public static final String VAPI_ENV_ASSISTANT_ID    = "VAPI_ASSISTANT_ID";
    public static final String VAPI_KEY_ASSISTANT_ID    = "assistantId";
    public static final String VAPI_KEY_PHONE_NUMBER_ID = "phoneNumberId";
    public static final String VAPI_KEY_CUSTOMER        = "customer";
    public static final String VAPI_KEY_CUSTOMER_NUMBER = "number";
    public static final String VAPI_KEY_OVERRIDES       = "assistantOverrides";
    public static final String VAPI_KEY_FIRST_MESSAGE   = "firstMessage";

    public static final String TWILIO_ENV_ACCOUNT_SID  = "TWILIO_ACCOUNT_SID";
    public static final String TWILIO_ENV_AUTH_TOKEN   = "TWILIO_AUTH_TOKEN";
    public static final String TWILIO_ENV_FROM_NUMBER  = "TWILIO_FROM_NUMBER";
    public static final String TWILIO_HEADER_AUTH      = "Authorization";
    public static final String TWILIO_AUTH_PREFIX      = "Basic ";
    public static final String TWILIO_FORM_TO          = "To";
    public static final String TWILIO_FORM_FROM        = "From";
    public static final String TWILIO_FORM_TWIML       = "Twiml";
    public static final String TWILIO_CALLS_PATH       = "/Calls.json";
    public static final String TWILIO_CRED_SEPARATOR   = ":";
    
    public static final String KEY_OPEN                  = "open";
    public static final String KEY_HIGH                  = "high";
    public static final String KEY_LOW                   = "low";
    public static final String KEY_CLOSE                 = "close";
    public static final String KEY_VOLUME                = "volume";
    public static final String KEY_SERIES                = "series";
    public static final String KEY_DAILY                 = "daily";
    public static final String KEY_TIME_SERIES           = "time series";
    public static final String KEY_SYMBOLS               = "symbols";
    public static final String KEY_SYMBOL                = "symbol";
    public static final String KEY_STATUS                = "status";
    public static final String KEY_BIDS                  = "bids";
    public static final String KEY_ASKS                  = "asks";
    public static final String KEY_LAST_PRICE            = "lastPrice";
    public static final String KEY_PRICE_CHANGE_PERCENT  = "priceChangePercent";
    public static final String KEY_HIGH_PRICE            = "highPrice";
    public static final String KEY_LOW_PRICE             = "lowPrice";

    public static final String STATUS_TRADING       = "TRADING";
    public static final String ASSET_TYPE_CRYPTO    = "CRYPTO";
    public static final String ASSET_TYPE_STOCK     = "STOCK";
    public static final String SUFFIX_USDT          = "USDT";

    public static final String DEFAULT_SYMBOL_BTCUSDT = "BTCUSDT";
    public static final String DEFAULT_INTERVAL_DAILY = "1d";
    public static final String APIKEY_DEMO            = "demo";
    public static final String TREND_BULLISH          = "BULLISH";
    public static final String TREND_BEARISH          = "BEARISH";
    public static final String TREND_SIDEWAYS         = "SIDEWAYS";
    public static final int    DEFAULT_LIMIT_FALLBACK = 30;

    private ApiEndpoints() {}
}
