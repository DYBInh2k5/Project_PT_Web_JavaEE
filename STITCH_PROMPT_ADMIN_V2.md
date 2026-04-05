You are Stitch. Design a complete, production-ready internal admin interface for a Java EE Servlet/JSP bookstore management system named "Bookora Admin".

Primary objective:
Create a bold, modern operations dashboard and management UI for staff/admin users, ready to transplant into JSP pages while preserving backend form submissions and server-side placeholders.

Visual direction:
- Mood: confident operations control center, editorial-meets-finance aesthetic.
- Typography: expressive high-contrast serif for page titles, clean neo-grotesk sans for body and tables.
- Color system: warm light neutral base, graphite text, forest/teal accents, coral for destructive actions.
- Background treatment: subtle grain/pattern + layered gradients (not flat white).
- Avoid generic boilerplate admin templates.

Technical constraints:
- Output semantic HTML and utility-class-friendly markup (Tailwind-compatible).
- No React/Vue assumptions.
- JS only for simple interactions (dropdowns, tabs, filters, toasts).
- Mobile + desktop responsive.
- Accessible labels, keyboard focus states, high contrast for table actions.

Required screens and routes:
1) Login (/login)
- Clean split layout, branded panel, login card.
- Fields: username, password, remember-me.
- Error state and loading state.

2) Dashboard (/dashboard)
- KPI cards: total books, low stock count, today revenue, monthly revenue.
- Revenue trend chart placeholder.
- Top selling books widget.
- Recent invoices timeline/table.
- Quick actions panel.

3) Books list (/books)
- Search + filters (category, stock status).
- Table columns: code, title, author, price, stock, status, actions.
- Batch actions and pagination UI.
- Low-stock badge styling.

4) Book form (/books/new, /books/edit)
- Structured form with sections: basic info, pricing, inventory, category/publisher.
- Validation and helper text styles.
- Sticky action footer (Save, Save & New, Cancel).

5) Customers list and form (/customers, /customers/new, /customers/edit)
- Customer table with contact summary and order count placeholder.
- Form blocks for profile/contact/address.

6) Invoices list (/invoices)
- Date range filter, status filter, keyword search.
- Invoice table with totals and quick-view action.

7) New invoice (/invoices/new)
- Two-column workflow: customer section + product picker.
- Selected items table with qty steppers, discount row, grand total.
- Coupon input + feedback states.

8) Invoice detail (/invoices/detail)
- Summary header card + line items + payment breakdown.
- Print/download action group style.

9) Promotions (/promotions, /promotions/new, /promotions/edit)
- Promotion list cards/table with status chips.
- Form supports: %, COUPON, TANG1.
- Explain TANG1 rule area (e.g., Buy N get 1).

10) Returns and purchases (/returns/new, /purchases/new)
- Transaction entry layouts with item lines and totals.
- Warning callouts for stock impacts.

11) Reports (/reports/revenue)
- Filter panel (from/to date, granularity).
- Revenue chart placeholder and top books ranking table.
- Export buttons style (Excel/PDF).

12) Error pages (403, 404, 500)
- Branded but minimal error pages with clear recovery CTA.

Shared components required:
- Sidebar navigation (collapsible), top bar, breadcrumb.
- Reusable table style, form controls, badges, alerts, modal, confirm dialog.
- Toast notifications: success, warning, error, info.
- Empty states and skeleton loading blocks.

Interaction/motion guidance:
- Subtle page-enter animation and staggered card reveals.
- Hover elevation for cards and row highlight for tables.
- Clear pressed/focus/disabled button states.

JSP integration hints:
- Preserve placeholder tokens like {{contextPath}}, {{user.name}}, {{books}}, {{customers}}, {{invoiceItems}}, {{promotion.type}}, {{errorMessage}}.
- Keep forms method/action ready for servlet endpoints.
- Keep element IDs stable and predictable for JSP binding.

Deliverables:
- Provide one cohesive design system plus all listed screens.
- Include top CSS variable tokens section.
- Include route-to-component mapping table for Java EE JSP integration.

Final bar:
- Must feel like a premium, intentional bookstore operations product.
- Must remain practical for immediate JSP adoption without frontend framework rewrite.
