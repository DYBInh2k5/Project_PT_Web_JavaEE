You are Stitch. Design a complete, production-ready public-facing online bookstore experience named "Bookora" for a Java EE Servlet/JSP app.

Goal:
Create premium ecommerce UI screens that can be transplanted into JSP pages while preserving server-side binding placeholders and forms.

Brand direction:
- Mood: modern literary boutique, warm and editorial but still fast and commerce-focused.
- Visual identity: refined serif headlines + clean geometric sans body text.
- Color system: ivory paper background, deep ink text, moss/emerald accents, amber highlight for CTA.
- Avoid generic SaaS dashboard look.
- No dark-mode-first bias.

Technical constraints:
- Output semantic HTML + utility-class-friendly structure (Tailwind-compatible).
- Keep forms simple and backend-friendly for JSP/Servlet integration.
- Do not rely on React/Vue client state.
- Use lightweight JS only for basic UI interactions.
- Responsive from mobile to desktop.
- Accessible: proper contrast, labels, aria attributes, keyboard navigation.

Required screens:
1) Shop Home (/shop)
- Hero section with search bar.
- Product grid cards (book cover, title, author, stock, price, add-to-cart).
- Top nav with links: Shop, Cart, My Orders, Order Lookup.
- Promo strip showing active coupon examples.

2) Book Detail (/shop/book)
- Large cover, metadata, pricing, stock badge.
- Quantity selector + add to cart button.
- Related books carousel/row.

3) Cart (/shop/cart)
- Editable line items table/list (qty controls, remove).
- Live subtotal block.
- Continue shopping + Checkout CTA.
- Empty cart state illustration placeholder.

4) Checkout (/shop/checkout)
- Customer info form (name/phone/email/address).
- Coupon field + explanation block for %, COUPON, TANG1.
- Order summary sidebar.
- Success alert style for showing Invoice ID + OrderCode.

5) My Orders (/shop/my-orders)
- Session-based order history list/table.
- Columns: OrderCode, InvoiceID, Date, ItemCount, Total, Action.
- Action button to open order detail.

6) Order Lookup (/shop/order-lookup)
- Lookup form by InvoiceID + phone/email.
- Result card + itemized order table.
- Link to open detailed order page.

7) Order Detail (/shop/order?code=...)
- Order header card (OrderCode, Invoice ID, date, customer, phone, total).
- Verification panel state (phone/email verify) for unauthorized sessions.
- Itemized table.
- Download PDF invoice button.

8) Shared components
- Header/nav variants.
- Alert/toast styles: success, warning, error, info.
- Buttons, input fields, badges, empty states.

Design details:
- Typography scale with clear rhythm and strong hierarchy.
- Soft shadows, rounded corners, subtle gradients/patterns.
- Micro-motion: hover elevation, button press states, section reveal transitions.
- Ensure visual consistency across all pages.

Deliverables:
- Provide all screens as clean HTML sections with clear comments for each route block.
- Include reusable style token section (CSS variables) at top.
- Include a short mapping table: route -> main components -> expected JSP placeholders.

JSP placeholder hints:
- Use placeholder markers like {{contextPath}}, {{book.title}}, {{cartCount}}, {{orders}} where needed.
- Keep placeholders obvious and easy to replace with JSP expressions.

Final quality bar:
- Should look like a polished commercial bookstore, not a template demo.
- Must be implementation-friendly for Java EE JSP pages without frontend framework rewrites.
