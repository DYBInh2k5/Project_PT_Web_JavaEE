# Huong dan cau hinh Stitch MCP an toan

Ngay cap nhat: 2026-04-05

## Da thuc hien
- Da bo hardcode API key trong `.vscode/mcp.json`.
- Da doi sang bien moi truong:
  - `X-Goog-Api-Key: ${env:STITCH_API_KEY}`
- Da set bien moi truong user tren Windows:
  - `STITCH_API_KEY`

## Ban can lam tiep
1. Dong va mo lai VS Code (hoac `Developer: Reload Window`).
2. Mo Copilot Chat Agent va kiem tra Stitch MCP da san sang.
3. Dan prompt thiet ke UI cho Stitch.

## Luu y bao mat
- Khong commit API key dang plain text vao file trong workspace.
- Neu key da tung lo, nen rotate key tren Google Cloud va cap nhat lai bien moi truong.
